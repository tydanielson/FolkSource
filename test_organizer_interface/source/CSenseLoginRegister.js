enyo.kind({
    name: "CSenseLoginRegister",
    classes: "dark-background",
	kind: onyx.Popup,
    published: {
        register: !1,
		autoDismiss: false,
		centered: true,
		floating: true,
		modal: true,
		scrimWhenModal: false,
		scrim: true,
	},
    events: {
		onSuccessCode: "",
		onFailureCode: ""
	},
    components: [
		{name: "rbox", kind: "onyx.RadioGroup", style: "width: 100%;", components: [
			{content: "Login", active: !0, classes: "onyx-radiobutton", ontap: "setupLogin"},
			{content: "Register", classes: "onyx-radiobutton", ontap: "setupRegister"}
		]},
		{name: "gbox", kind: "onyx.Groupbox", components: []},
		{name: "memoryBox", kind: "onyx.Checkbox", style: "clear: left; float: left;", ontap: "remember"},
		{name: "text", content: "Remember Me", style: "float: left;"}
	],
    create: function () {
        this.inherited(arguments);
		/*if(LocalStorage.get("remember") == undefined) {
			LocalStorage.remove("uid");
			LocalStorage.remove("points");
		}*/
		if(this.register) {
			this.$.radioButton.setActive(false);
			this.$.radioButton2.setActive(true);
			this.setupRegister();
		} else 
			this.setupLogin();
  },
    rendered: function () {
        this.inherited(arguments);
  },
    setupLogin: function () {
        this.register = !1;
		this.$.gbox.destroyClientControls();
		this.$.gbox.createComponent({name: "gboxhead", kind: "onyx.GroupboxHeader", content: "Login"}, {owner: this});
		this.$.gbox.createComponent({name: "gboxcomp", components: [{name: "userdec", kind: "onyx.InputDecorator", classes: "onyx-input-decorator", components: [{name: "username", kind: "onyx.Input", placeholder: "Username", defaultFocus: !0, type: "email", style: "width: 100%;", classes: "onyx-input", onkeyup: "checkFields"}]}, {name: "passdec", kind: "onyx.InputDecorator", classes: "onyx-input-decorator", components: [{name: "password", kind: "onyx.Input", placeholder: "Password", type: "password", style: "width: 100%;", classes: "onyx-input", onkeyup: "checkFields"}]}, {name: "logButton", kind: "onyx.Button", content: "Login", style: "clear: both;", ontap: "buildURL"}]}, {owner: this});
		this.$.gbox.render();
	},
    setupRegister: function () {
        this.register = !0;
		this.$.gbox.destroyClientControls();
		this.$.gbox.createComponent({name: "gboxhead", kind: "onyx.GroupboxHeader", content: "Register"}, {owner: this});
		this.$.gbox.createComponent({name: "gboxcomp", components: [{name: "emaildec", kind: "onyx.InputDecorator", classes: "onyx-input-decorator", components: [{name: "email", kind: "onyx.Input", placeholder: "E-Mail", defaultFocus: !0, style: "width: 100%;", classes: "onyx-input", onkeyup: "emailRegexCheck"}]}, {name: "userdec", kind: "onyx.InputDecorator", classes: "onyx-input-decorator", components: [{name: "username", kind: "onyx.Input", placeholder: "Username", type: "email", style: "width: 100%;", classes: "onyx-input", onkeyup: "checkFields"}]}, {name: "passdec", kind: "onyx.InputDecorator", classes: "onyx-input-decorator", components: [{name: "password", kind: "onyx.Input", placeholder: "Password", type: "password", style: "width: 100%;", classes: "onyx-input", onkeyup: "checkFields"}]}, {name: "logButton", kind: "onyx.Button", content: "Register", disabled: !0, style: "clear: both;", ontap: "buildURL"}]}, {owner: this});
		this.$.gbox.render();
	},
    buildURL: function () {
        this.log();
        if (this.checkFields()) {
            this.$.logButton.setDisabled(!1);
            var a = "login?";
            if(this.register) {
				a = "user?";
				a += "email=" + this.$.email.getValue() + "&";
			}
			a += "name=" + this.$.username.getValue() + "&";
			a += "password=" + this.$.password.getValue();
            var b = (new enyo.Ajax({
                method: "POST",
                url: Data.getURL() + a,
				headers: {"Cache-Control": "no-cache"},
				cacheBust: true,
                handleAs: "text"
          })).go().response(this, "handleResponse");
      }
  },
    handleResponse: function (a, b) {
	this.log(a.xhr);
        if (a.xhr.status === 200) {
			var incoming = JSON.parse(a.xhr.responseText);
			this.log(incoming.points);
			this.log(incoming.uid);
			this.log("WEEE");
			this.hide();
			LocalStorage.set("points", JSON.stringify(incoming.points));
			LocalStorage.set("user", JSON.stringify(incoming.uid));
			LocalStorage.set("username", JSON.stringify(incoming.name));
		if(this.$.memoryBox.getValue())
			LocalStorage.set("remember", true);
			this.doSuccessCode();
		} else {
			this.log(JSON.stringify(a));
			this.log(a.xhr.status);
			this.log("BOOO");
			this.doFailureCode();
		}
  },
    emailRegexCheck: function () {
        var a = /^\w+([\.\+]\w+)*@\w+(\.\w+)*(\.\w{2,})$/;
        return a.test(this.$.email.getValue()) ? (this.$.email.applyStyle("color", "black"), !0) : (this.$.email.applyStyle("color", "red"), !1);
  },
    checkUsernameExists: function () {
        return this.$.username.getValue() === "" ? !1 : !0;
  },
    checkPasswordExists: function () {
        return this.$.password.getValue() === "" ? !1 : !0;
  },
    checkFields: function () {
        if (this.checkUsernameExists() && this.checkPasswordExists()) return this.register && this.emailRegexCheck ? (this.$.logButton.setDisabled(!1), !0) : (this.$.logButton.setDisabled(!1), !0);
        return !1;
  }
});
