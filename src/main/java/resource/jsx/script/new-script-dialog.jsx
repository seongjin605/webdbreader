var React = require('react');
var MaterialWrapper = require('../comps/material-wrapper.jsx');
var Button = MaterialWrapper.Button;
var FlatButton = MaterialWrapper.FlatButton;
var Dialog = MaterialWrapper.Dialog;
var ImportVer1ScriptDialog = require('./new-script-dialog/import-ver1-script-dialog.jsx');

var NewScriptDialog = React.createClass({
	getInitialState() {
		return {
			visible: false
		};
	},

	show() {
		this.setState({ visible: true });
	},

	hide(callback) {
		this.setState({ visible: false }, callback);
	},

	goDb2File(evt) {
		evt.stopPropagation();
		window.location.href = '/Script/NewDb2File';
	},

	goDb2Db(evt) {
		evt.stopPropagation();
		window.location.href = '/Script/NewDb2Db';
	},

	goImportVer1Script(evt) {
		evt.stopPropagation();
		this.hide(function() {
			this.refs.importVer1scriptDialog.show();
		});
	},

	goNewScript(evt) {
		evt.stopPropagation();
		alert('not implments');
		//TODO
	},

	render() {
		return (
			<div>
				<Dialog
					title="new script"
					actions={[
						{ text: 'close', onClick: this.hide }
					]}
					actionFocus="close"
					open={this.state.visible}
					onRequestClose={this.hide}
					autoDetectWindowHeight={true}
					autoScrollBodyContent={true}>
					<FlatButton 
						label=">> database to file"
						style={{ width: '100%', textAlign: 'left' }}
						onClick={this.goDb2File} />
					<FlatButton 
						label=">> database to database"
						style={{ width: '100%', textAlign: 'left' }}
						onClick={this.goDb2Db} />
					<FlatButton 
						label=">> import version 1 script"
						style={{ width: '100%', textAlign: 'left' }}
						onClick={this.goImportVer1Script} />
					<FlatButton 
						label=">> new script"
						style={{ width: '100%', textAlign: 'left' }}
						onClick={this.goNewScript} />
				</Dialog>
				<ImportVer1ScriptDialog ref="importVer1scriptDialog" />
			</div>
		);
	}
});
module.exports = NewScriptDialog;