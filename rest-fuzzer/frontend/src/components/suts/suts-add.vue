<template>
  <b-card v-if="display" header-tag="header" footer-tag="footer">
    <template v-slot:header>
      <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;Add system under test
    </template>

    <b-card-text>
      <b-form>
        <b-form-group
          id="input-group-1"
          label="OAS location:"
          label-for="input-1"
          description="Url to OpenAPI specification"
        >
          <b-form-input
            id="input-1"
            v-model="sut.location"
            placeholder="enter url to OpenAPI specification"
          ></b-form-input>
        </b-form-group>
      </b-form>
    </b-card-text>

    <template v-slot:footer>
      <div class="button-group-right">
        <b-button size="sm" type="submit" variant="primary" @click="addSut()">
          <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
        </b-button>
        <b-button size="sm" type="cancel" variant="outline-secondary" @click="clear()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp;clear
        </b-button>
      </div>
    </template>
  </b-card>
</template>

<script>
export default {
  data() {
    return {
      sut: {
        location: ""
      }
    };
  },
  computed: {
    display() {
      return (
        this.$store.getters.suts.display !== null &&
        this.$store.getters.suts.display === "add"
      );
    }
  },
  methods: {
    reset() {
      this.sut.location = "";
    },
    clear() {
      this.reset();
      this.$store.commit("set_sut_display", { display: null });
    },
    addSut() {
      this.$store.dispatch("addSut", this.sut).then(() => {
        this.clear();
        this.$store.dispatch("findAllSuts");
      });
    }
  }
};
</script>