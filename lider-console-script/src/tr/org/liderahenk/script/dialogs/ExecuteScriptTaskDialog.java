package tr.org.liderahenk.script.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.script.constants.ScriptConstants;
import tr.org.liderahenk.script.i18n.Messages;
import tr.org.liderahenk.script.model.ScriptFile;

/**
 * Task execution dialog for script plugin.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class ExecuteScriptTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteScriptTaskDialog.class);

	private Combo cmbScriptFile;
	private Text txtScriptParams;

	public ExecuteScriptTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("EXECUTE_SCRIPT");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblScript = new Label(composite, SWT.NONE);
		lblScript.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblScript.setText(Messages.getString("SCRIPT_LABEL"));

		cmbScriptFile = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbScriptFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		try {
			IResponse response = TaskRestUtils.execute(ScriptConstants.PLUGIN_NAME, ScriptConstants.PLUGIN_VERSION,
					"LIST_SCRIPTS");
			if (response != null && response.getResultMap() != null && response.getResultMap().get("SCRIPTS") != null) {
				List<ScriptFile> scripts = new ObjectMapper().readValue(
						response.getResultMap().get("SCRIPTS").toString(), new TypeReference<List<ScriptFile>>() {
						});
				if (scripts != null && !scripts.isEmpty()) {
					for (int i = 0; i < scripts.size(); i++) {
						ScriptFile script = scripts.get(i);
						cmbScriptFile.add(script.getLabel() + " " + script.getCreateDate());
						cmbScriptFile.setData(i + "", script);
					}
					cmbScriptFile.select(0);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}

		Label lblScriptParams = new Label(composite, SWT.NONE);
		lblScriptParams.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblScriptParams.setText(Messages.getString("SCRIPT_PARAMETERS"));

		txtScriptParams = new Text(composite, SWT.BORDER);
		txtScriptParams.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return composite;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (getSelectedScript() == null) {
			throw new ValidationException(Messages.getString("SELECT_SCRIPT"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("SCRIPT_FILE_ID", getSelectedScript().getId());
		// SCRIPT_PARAMS may contain script parameters or it can be empty string
		parameterMap.put("SCRIPT_PARAMS", txtScriptParams.getText());
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return "EXECUTE_SCRIPT";
	}

	@Override
	public String getPluginName() {
		return ScriptConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ScriptConstants.PLUGIN_VERSION;
	}

	private ScriptFile getSelectedScript() {
		int selectionIndex = cmbScriptFile.getSelectionIndex();
		if (selectionIndex > -1 && cmbScriptFile.getItem(selectionIndex) != null
				&& cmbScriptFile.getData(selectionIndex + "") != null) {
			return (ScriptFile) cmbScriptFile.getData(selectionIndex + "");
		}
		return null;
	}

}
