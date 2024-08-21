package ru.runa.wf.web.ftl.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import ru.runa.wfe.commons.ftl.FormComponentSubmissionPostProcessor;
import ru.runa.wfe.var.UserTypeMap;
import ru.runa.wfe.var.VariableDefinition;
import ru.runa.wfe.var.dto.WfVariable;
import ru.runa.wfe.var.format.BooleanFormat;
import ru.runa.wfe.var.format.VariableFormatContainer;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;


public class EditUserTypeList extends AbstractUserTypeList implements FormComponentSubmissionPostProcessor {
    private static final long serialVersionUID = 1L;

    @Override
    protected UserTypeListModel parseParameters() {
        String variableName = getParameterAsString(0);
        WfVariable variable = variableProvider.getVariableNotNull(variableName);
        boolean allowToAddElements = getParameterAs(boolean.class, 1);
        boolean allowToDeleteElements = getParameterAs(boolean.class, 2);
        boolean allowToChangeElements = getParameterAs(boolean.class, 3);
        List<String> attributeNames = getMultipleParameter(4);
        return new EditUserTypeListModel(variable, attributeNames, allowToAddElements, allowToChangeElements, allowToDeleteElements);
    }

    @Override
    public Object postProcessValue(Object input) throws Exception {
        List<UserTypeMap> list = (List<UserTypeMap>) input;
        if (!list.isEmpty()) {
            // reset boolean values which are not presented in columns list (#152#note-26).
            List<String> attributeNames = getMultipleParameter(4);
            for (VariableDefinition definition : list.get(0).getUserType().getAttributes()) {
                if (BooleanFormat.class.getName().equals(definition.getFormat()) && !attributeNames.contains(definition.getName())) {
                    for (UserTypeMap userTypeMap : list) {
                        userTypeMap.remove(definition.getName());
                    }
                }
            }
        }
        return list;
    }

    public class EditUserTypeListModel extends UserTypeListModel {
        private final boolean allowToAddElements;
        private final boolean allowToChangeElements;
        private final boolean allowToDeleteElements;

        public EditUserTypeListModel(WfVariable variable, List<String> attributeNames, boolean allowToAddElements, boolean allowToChangeElements,
                boolean allowToDeleteElements) {
            super(variable, attributeNames, true);
            this.allowToAddElements = allowToAddElements;
            this.allowToChangeElements = allowToChangeElements;
            this.allowToDeleteElements = allowToDeleteElements;
        }

        public boolean isAllowToAddElements() {
            return allowToAddElements;
        }

        public boolean isAllowToDeleteElements() {
            return allowToDeleteElements;
        }

        @Override
        public String getValue(TemplateModel arg0, TemplateModel arg1, Number index) throws TemplateModelException {
            if (allowToChangeElements) {
                UserTypeMap userTypeMap = (UserTypeMap) BEANS_WRAPPER.unwrap(arg0);
                VariableDefinition attributeDefinition = (VariableDefinition) BEANS_WRAPPER.unwrap(arg1);
                WfVariable variable = getAttributeVariable(userTypeMap, attributeDefinition, index);
                return ViewUtil.getComponentInput(user, webHelper, variable);
            }
            return super.getValue(arg0, arg1, index);
        }

        public String getTemplateValue(TemplateModel arg0) throws TemplateModelException {
            VariableDefinition definition = (VariableDefinition) BEANS_WRAPPER.unwrap(arg0);
            String suffix = VariableFormatContainer.COMPONENT_QUALIFIER_START + VariableFormatContainer.COMPONENT_QUALIFIER_END + "."
                    + definition.getName();
            WfVariable templateComponentVariable = ViewUtil.createComponentVariable(variable, suffix, definition.getFormatNotNull(), null);
            String inputComponentHtml = ViewUtil.getComponentInput(user, webHelper, templateComponentVariable);
//            return inputComponentHtml.replaceAll("\"", "'").replaceAll("\n", "").replace("[]", "{}");
            return replaceByDefaultNew(inputComponentHtml);
        }
    }

    public String replaceByDefaultOld(String stringInput) {
        return stringInput.replaceAll("\"", "'")
                .replaceAll("\n", "").replace("[]", "{}");
    }

    public String replaceByDefaultNew(String stringInput) {
        StringBuilder stringBuilder = new StringBuilder(stringInput);
        Map<String, String> replacements = new HashMap<String, String>() {{
            put("\"", "'");
            put("\n", "");
            put("[]", "{}");
        }};
        String[] stringToReplace = {"\"", "\n", "[]"};
        for (int i = 0; i < stringToReplace.length; i++) {
            int index = stringBuilder.length() - 1;
            while (index >= 0 && index < stringBuilder.length()) {
                index = stringBuilder.lastIndexOf(stringToReplace[i], index);
                if (index != -1)
                    stringBuilder.replace(index,
                            index + stringToReplace[i].length(),
                            replacements.get(stringToReplace[i]));
            }
        }
        return stringBuilder.toString();
    }

    public String replaceByDefaultNew2(String stringInput) {
        Map<String, String> replacements = new HashMap<String, String>() {{
            put("\"", "'");
            put("\n", "");
            put("[]", "{}");
        }};
        String regexp = "\"|\n|\\[]";

        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(stringInput);

        while (m.find())
            m.appendReplacement(sb, replacements.get(m.group()));
        m.appendTail(sb);

        return sb.toString();
    }

}