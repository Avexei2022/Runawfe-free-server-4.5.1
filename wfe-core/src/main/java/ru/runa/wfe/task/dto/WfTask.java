/*
 * This file is part of the RUNA WFE project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package ru.runa.wfe.task.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import lombok.Getter;
import lombok.Setter;
import ru.runa.wfe.task.Task;
import ru.runa.wfe.task.TaskDeadlineUtils;
import ru.runa.wfe.user.Actor;
import ru.runa.wfe.user.DelegationGroup;
import ru.runa.wfe.user.Executor;
import ru.runa.wfe.user.Group;
import ru.runa.wfe.var.dto.WfVariable;

/**
 * Process task.
 * 
 * @author Dofs
 * @since 4.0
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class WfTask implements Serializable {
    private static final long serialVersionUID = 3415182898189086844L;

    private Long id;
    private String name;
    private String nodeId;
    private String description;
    private String swimlaneName;
    private Executor owner;
    private Actor targetActor;
    private Long definitionId;
    private Long rootDefinitionId;
    private String definitionName;
    private String rootDefinitionName;
    private Long processId;
    private Long rootProcessId;
    private String processHierarchyIds;
    private Long tokenId;
    private Date creationDate;
    private Date deadlineDate;
    private Date deadlineWarningDate;
    private Date assignDate;
    private boolean escalated;
    private boolean firstOpen;
    private boolean acquiredBySubstitution;
    private Integer multitaskIndex;
    private boolean readOnly;

    // map is not usable in web services
    private final List<WfVariable> variables = Lists.newArrayList();

    public WfTask() {
    }

    public WfTask(Task task, Long rootProcessId, Long rootDefinitionId, String rootDefinitionName, Long definitionId, String definitionName,
            Actor targetActor, boolean escalated, boolean acquiredBySubstitution, boolean firstOpen) {
        this.id = task.getId();
        this.name = task.getName();
        this.nodeId = task.getNodeId();
        this.description = task.getDescription();
        this.owner = task.getExecutor();
        this.processId = task.getProcess().getId();
        this.rootProcessId = rootProcessId;
        this.processHierarchyIds = task.getProcess().getHierarchyIds();
        this.tokenId = task.getToken().getId();
        this.rootDefinitionId = rootDefinitionId;
        this.rootDefinitionName = rootDefinitionName;
        this.definitionId = definitionId;
        this.definitionName = definitionName;
        this.swimlaneName = task.getSwimlane() != null ? task.getSwimlane().getName() : "";
        this.creationDate = task.getCreateDate();
        this.deadlineDate = task.getDeadlineDate();
        this.deadlineWarningDate = TaskDeadlineUtils.getDeadlineWarningDate(task);
        this.assignDate = task.getAssignDate();
        this.targetActor = targetActor;
        this.escalated = escalated;
        this.acquiredBySubstitution = acquiredBySubstitution;
        this.firstOpen = firstOpen;
        this.multitaskIndex = task.getIndex();
    }

    public boolean isDelegated() {
        return owner instanceof DelegationGroup;
    }

    public boolean isGroupAssigned() {
        return owner instanceof Group;
    }

    public void addVariable(WfVariable variable) {
        if (variable != null) {
            variables.add(variable);
        }
    }

    public WfVariable getVariable(String name) {
        for (WfVariable variable : variables) {
            if (Objects.equal(name, variable.getDefinition().getName())) {
                return variable;
            }
        }
        return null;
    }

    public Object getVariableValue(String name) {
        WfVariable variable = getVariable(name);
        if (variable != null) {
            return variable.getValue();
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WfTask) {
            return Objects.equal(id, ((WfTask) obj).id);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("definitionId", definitionId).add("processId", processId).add("id", id).add("name", name).toString();
    }

}
