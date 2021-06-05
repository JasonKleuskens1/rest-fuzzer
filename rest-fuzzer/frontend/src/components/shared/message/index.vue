<template></template>

<script>
export default {
  data() {
    return {
      toast: this.$toast,
      timeout: null
    };
  },
  methods: {
	displayMessage(message) {
	  switch(message.type) {
		case "info": this.displayInfo(message.title, message.text);
		break;
		case "error":
			if (message.err.response.status === 400) {
				this.displayWarning(message.text, message.err);				
			} else {
				this.displayError(message.text, message.err);
			}
		break;
	  }
	},
    displayInfo(title, text) {
      this.$bvToast.toast(text, {
        title: title,
        variant: "primary",
        noAutoHide: false,
        appendToast: true
      });
    },
    displayWarning(title, error) {
      this.$bvToast.toast(this.getMsg(error), {
          title: title,
          variant: "warning",
          noAutoHide: false,
          appendToast: true
        }
      );
    },
    displayError(title, error) {
      this.$bvToast.toast(this.getMsg(error), {
        title: title,
        variant: "danger",
        noAutoHide: false,
        autoHideDelay: 20000,
        appendToast: true
      });
    },
    getMsg(error) {
      const h = this.$createElement;

      let violoations = [];
      if (error.response.data.violations) {
        error.response.data.violations.forEach(v =>
          violoations.push(h("li", { style: "font-style: italic;" }, v))
        );
      }

      let msg = h("span", {}, [
        h(
          "div",
          { style: "margin: 10px 0px 15px 0px;" },
          `${error.response.statusText} (${error.response.status}):`
        ),
        violoations.length === 0 ? "-" : h("ul", {}, violoations)
      ]);

      return msg;
    },
    displayMessages() {
      this.messages.forEach(msg => this.displayMessage(msg));
      this.$store.commit('messages_clear');
      this.timeout = setTimeout(this.displayMessages, 100);
    }
  },
  computed: {
    messages: function() {
      return this.$store.getters.messages;
    }
  },
  destroyed: function() {
    clearTimeout(this.timeout);
  },
  created: function() {
    this.displayMessages();
  }
};
</script>
