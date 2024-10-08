/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package ru.runa.wfe.execution;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import ru.runa.wfe.audit.ProcessCancelLog;
import ru.runa.wfe.audit.ProcessEndLog;
import ru.runa.wfe.commons.ApplicationContextFactory;
import ru.runa.wfe.commons.ClassLoaderUtil;
import ru.runa.wfe.commons.SystemProperties;
import ru.runa.wfe.definition.Deployment;
import ru.runa.wfe.definition.dao.ProcessDefinitionLoader;
import ru.runa.wfe.extension.ProcessEndHandler;
import ru.runa.wfe.job.dao.JobDao;
import ru.runa.wfe.lang.AsyncCompletionMode;
import ru.runa.wfe.lang.BaseTaskNode;
import ru.runa.wfe.lang.Node;
import ru.runa.wfe.lang.ProcessDefinition;
import ru.runa.wfe.lang.SubprocessNode;
import ru.runa.wfe.lang.Synchronizable;
import ru.runa.wfe.security.SecuredObjectBase;
import ru.runa.wfe.security.SecuredObjectType;
import ru.runa.wfe.task.Task;
import ru.runa.wfe.task.TaskCompletionInfo;
import ru.runa.wfe.user.Actor;
import ru.runa.wfe.user.TemporaryGroup;
import ru.runa.wfe.user.dao.ExecutorDao;

/**
 * Is one execution of a {@link ru.runa.wfe.lang.ProcessDefinition}.
 */
@Entity
@Table(name = "BPM_PROCESS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Process extends SecuredObjectBase {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(Process.class);

    private Long id;
    private Long parentId;
    private Long version;
    private Date startDate;
    private Date endDate;
    private Token rootToken;
    private String hierarchyIds;
    private Deployment deployment;
    private ExecutionStatus executionStatus = ExecutionStatus.ACTIVE;
    private Long externalData;

    public Process() {
    }

    public Process(Deployment deployment) {
        setDeployment(deployment);
        setStartDate(new Date());
    }

    @Transient
    @Override
    public SecuredObjectType getSecuredObjectType() {
        return SecuredObjectType.PROCESS;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
    @SequenceGenerator(name = "sequence", sequenceName = "SEQ_BPM_PROCESS", allocationSize = 1)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "PARENT_ID")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Version
    @Column(name = "VERSION")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "TREE_PATH", length = 1024)
    public String getHierarchyIds() {
        return hierarchyIds;
    }

    public void setHierarchyIds(String hierarchyIds) {
        this.hierarchyIds = hierarchyIds;
    }

    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @ManyToOne(targetEntity = Deployment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFINITION_ID", nullable = false)
    @ForeignKey(name = "FK_PROCESS_DEFINITION")
    @Index(name = "IX_PROCESS_DEFINITION")
    public Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    @ManyToOne(targetEntity = Token.class, fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL })
    @JoinColumn(name = "ROOT_TOKEN_ID", nullable = false)
    @ForeignKey(name = "FK_PROCESS_ROOT_TOKEN")
    @Index(name = "IX_PROCESS_ROOT_TOKEN")
    public Token getRootToken() {
        return rootToken;
    }

    public void setRootToken(Token rootToken) {
        this.rootToken = rootToken;
    }

    @Column(name = "EXECUTION_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    /**
     * Inherited by subprocesses (copied from parent process to subprocess on subprocess creation).
     */
    @Column(name = "EXTERNAL_DATA")
    public Long getExternalData() {
        return externalData;
    }

    public void setExternalData(Long clientData) {
        this.externalData = clientData;
    }

    /**
     * Ends this process and all the tokens in it.
     * 
     * @param canceller
     *            actor who cancels process (if any), can be <code>null</code>
     */
    public void end(ExecutionContext executionContext, Actor canceller) {
        if (hasEnded()) {
            log.debug(this + " already ended");
            return;
        }
        log.info("Ending " + this + " by " + canceller);

        if (canceller != null) {
            executionContext.addLog(new ProcessCancelLog(canceller));
        } else {
            executionContext.addLog(new ProcessEndLog());
        }

        TaskCompletionInfo taskCompletionInfo = TaskCompletionInfo.createForProcessEnd(id);
        // end the main path of execution
        rootToken.end(executionContext.getProcessDefinition(), canceller, taskCompletionInfo, true);
        // mark this process as ended
        setEndDate(new Date());
        setExecutionStatus(ExecutionStatus.ENDED);
        // check if this process was started as a subprocess of a super
        // process
        NodeProcess parentNodeProcess = executionContext.getParentNodeProcess();
        if (parentNodeProcess != null && !parentNodeProcess.getParentToken().hasEnded()) {
            ProcessDefinitionLoader processDefinitionLoader = ApplicationContextFactory.getProcessDefinitionLoader();
            ProcessDefinition parentProcessDefinition = processDefinitionLoader.getDefinition(parentNodeProcess.getProcess());
            Node node = parentProcessDefinition.getNodeNotNull(parentNodeProcess.getNodeId());
            Synchronizable synchronizable = (Synchronizable) node;
            if (!synchronizable.isAsync()) {
                log.debug("Signalling to parent " + parentNodeProcess.getProcess());
                parentNodeProcess.getParentToken().signalOnSubprocessEnd(executionContext);
            }
        }

        // make sure all the timers for this process are canceled
        // after the process end updates are posted to the database
        JobDao jobDao = ApplicationContextFactory.getJobDAO();
        jobDao.deleteByProcess(this);

        // flush just created tasks
        ApplicationContextFactory.getTaskDAO().flushPendingChanges();
        endAsyncActivitiesRecursively(executionContext, taskCompletionInfo, canceller);
        for (Swimlane swimlane : ApplicationContextFactory.getSwimlaneDAO().findByProcess(this)) {
            if (swimlane.getExecutor() instanceof TemporaryGroup) {
                swimlane.setExecutor(null);
            }
        }
        for (String processEndHandlerClassName : SystemProperties.getProcessEndHandlers()) {
            try {
                ProcessEndHandler handler = ClassLoaderUtil.instantiate(processEndHandlerClassName);
                ApplicationContextFactory.autowireBean(handler);
                handler.execute(executionContext);
            } catch (Throwable th) {
                Throwables.propagate(th);
            }
        }
        if (SystemProperties.deleteTemporaryGroupsOnProcessEnd()) {
            ExecutorDao executorDao = ApplicationContextFactory.getExecutorDAO();
            List<TemporaryGroup> groups = executorDao.getTemporaryGroups(id);
            for (TemporaryGroup temporaryGroup : groups) {
                if (ApplicationContextFactory.getProcessDAO().getDependentProcessIds(temporaryGroup).isEmpty()) {
                    log.debug("Cleaning " + temporaryGroup);
                    executorDao.remove(temporaryGroup);
                } else {
                    log.debug("Group " + temporaryGroup + " deletion postponed");
                }
            }
        }
    }

    protected void endAsyncActivitiesRecursively(ExecutionContext executionContext, TaskCompletionInfo taskCompletionInfo, Actor canceller) {
        boolean mainProcessForAsyncActivitiesIsActive = isMainProcessForAsyncActivitiesIsActive(executionContext);
        endAsyncTasks(executionContext, taskCompletionInfo, mainProcessForAsyncActivitiesIsActive);
        endAsyncSubprocesses(executionContext, canceller, mainProcessForAsyncActivitiesIsActive);
        // we should handle case of active subprocesses in ended ones
        for (Process subProcess : executionContext.getSubprocessesRecursively()) {
            if (subProcess.hasEnded()) {
                ProcessDefinition subProcessDefinition = ApplicationContextFactory.getProcessDefinitionLoader().getDefinition(subProcess);
                ExecutionContext subExecutionContext = new ExecutionContext(subProcessDefinition, subProcess);
                subProcess.endAsyncActivitiesRecursively(subExecutionContext, taskCompletionInfo, canceller);
            }
        }
    }

    private boolean isMainProcessForAsyncActivitiesIsActive(ExecutionContext executionContext) {
        final List<Long> processIdsReversed = Lists.newArrayList(ProcessHierarchyUtils.getProcessIds(hierarchyIds));
        Collections.reverse(processIdsReversed);
        for (Long processId : processIdsReversed) {
            NodeProcess nodeProcess = ApplicationContextFactory.getNodeProcessDAO().findBySubProcessId(processId);
            if (nodeProcess != null) {
                ProcessDefinition processDefinition = ApplicationContextFactory.getProcessDefinitionLoader().getDefinition(nodeProcess.getProcess());
                SubprocessNode subprocessNode = (SubprocessNode) processDefinition.getNode(nodeProcess.getNodeId());
                if (subprocessNode == null) {
                    // rm2834 can cause this
                    return false;
                }
                if (subprocessNode.isAsync() && subprocessNode.getCompletionMode() == AsyncCompletionMode.NEVER) {
                    return !nodeProcess.getSubProcess().hasEnded();
                }
            }
        }
        Long rootProcessId = ProcessHierarchyUtils.getRootProcessId(hierarchyIds);
        return !ApplicationContextFactory.getProcessDAO().get(rootProcessId).hasEnded();
    }

    private void endAsyncTasks(ExecutionContext executionContext, TaskCompletionInfo taskCompletionInfo, boolean mainProcessForAsyncActivitiesIsActive) {
        for (Task task : ApplicationContextFactory.getTaskDAO().findByProcess(this)) {
            BaseTaskNode taskNode = (BaseTaskNode) executionContext.getProcessDefinition().getNodeNotNull(task.getNodeId());
            if (taskNode.isAsync()) {
                switch (taskNode.getCompletionMode()) {
                case NEVER:
                    continue;
                case ON_MAIN_PROCESS_END:
                    if (mainProcessForAsyncActivitiesIsActive) {
                        continue;
                    }
                case ON_PROCESS_END:
                }
                task.end(executionContext, taskNode, taskCompletionInfo);
            }
        }
    }

    private void endAsyncSubprocesses(ExecutionContext executionContext, Actor canceller, boolean mainProcessForAsyncActivitiesIsActive) {
        for (Process subProcess : executionContext.getSubprocesses()) {
            if (!subProcess.hasEnded()) {
                NodeProcess nodeProcess = ApplicationContextFactory.getNodeProcessDAO().findBySubProcessId(subProcess.getId());
                SubprocessNode subprocessNode = (SubprocessNode) executionContext.getProcessDefinition().getNodeNotNull(nodeProcess.getNodeId());
                if (subprocessNode.isAsync()) {
                    switch (subprocessNode.getCompletionMode()) {
                    case NEVER:
                        continue;
                    case ON_MAIN_PROCESS_END:
                        if (mainProcessForAsyncActivitiesIsActive) {
                            continue;
                        }
                    case ON_PROCESS_END:
                    }
                    ProcessDefinition subProcessDefinition = ApplicationContextFactory.getProcessDefinitionLoader().getDefinition(subProcess);
                    ExecutionContext subExecutionContext = new ExecutionContext(subProcessDefinition, subProcess);
                    subProcess.end(subExecutionContext, canceller);
                }
            }
        }
    }

    /**
     * Tells if this process is still active or not.
     */
    public boolean hasEnded() {
        return executionStatus == ExecutionStatus.ENDED;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).add("status", executionStatus).toString();
    }

}
