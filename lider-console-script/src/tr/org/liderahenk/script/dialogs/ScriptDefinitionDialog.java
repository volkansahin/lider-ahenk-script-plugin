package tr.org.liderahenk.script.dialogs;

import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.script.editors.ScriptDefinitionEditor;
import tr.org.liderahenk.script.model.ScriptFile;

public class ScriptDefinitionDialog extends DefaultLiderDialog {

	private ScriptFile selectedScript;
	private ScriptDefinitionEditor editor;

	public ScriptDefinitionDialog(Shell parentShell, ScriptDefinitionEditor editor) {
		super(parentShell);
		this.editor = editor;
	}

	public ScriptDefinitionDialog(Shell parentShell, ScriptFile selectedScript, ScriptDefinitionEditor editor) {
		super(parentShell);
		this.selectedScript = selectedScript;
		this.editor = editor;
	}

}
