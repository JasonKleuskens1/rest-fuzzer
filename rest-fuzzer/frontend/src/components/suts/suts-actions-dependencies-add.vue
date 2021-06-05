<template>
  <b-modal id="suts-actions-dependencies-add" ref="modalActionsDependenciesAdd" centered size="lg">
    <template slot="modal-header">
      <h6>
        <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;Add action dependency
      </h6>
    </template>

    <b-form>
      <b-form-group label="Action:" label-for="action" description="Select an action">
        <b-form-select
          id="action"
          :options="actions"
          v-model="dependency.action"
          @change="updateParameters()"
        >
          <template v-slot:first>
            <b-form-select-option :value="null" disabled>-- select an action --</b-form-select-option>
          </template>
        </b-form-select>
      </b-form-group>

      <b-form-group label="Parameter:" label-for="parameter" description="Select a parameter">
        <b-form-select id="parameter" :options="parameters" v-model="dependency.parameter">
          <template v-slot:first>
            <b-form-select-option :value="null" disabled>-- select a parameter --</b-form-select-option>
          </template>
        </b-form-select>
      </b-form-group>

      <b-form-group
        label="Action (depends on):"
        label-for="actionDependsOn"
        description="Select the action this parameter depends on"
      >
        <b-form-select
          id="actionDependsOn"
          :options="actionsDependsOn"
          v-model="dependency.actionDependsOn"
        >
          <template v-slot:first>
            <b-form-select-option :value="null" disabled>-- select an action --</b-form-select-option>
          </template>
        </b-form-select>
      </b-form-group>

      <b-form-group
        label="Parameter (depends on):"
        label-for="parameter-depends-on"
        description="Type the name of the parameter in the response"
      >
        <b-form-input
          id="parameter-depends-on"
          v-model="dependency.parameterDependsOn"
        ></b-form-input>
      </b-form-group>
    </b-form>

    <template slot="modal-footer">
      <div class="button-group-right">
        <b-button size="sm" variant="primary" @click="add()">
          <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
        </b-button>
        <b-button size="sm" variant="outline-secondary" @click="clear()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp;clear
        </b-button>
      </div>
    </template>
  </b-modal>
</template>

<script>
export default {
  props: ["sut"],
  data() {
    return {
      dependency: {
        action: null,
        parameter: null,
        actionDependsOn: null,
        parameterDependsOn: null
      }
    };
  },
  computed: {
    actions() {
      return this.$store.getters.selectionActions;
    },
    parameters() {
      return this.$store.getters.selectionParameters;
    },
    actionsDependsOn() {
      return this.$store.getters.selectionActionsPosts;
    }
  },
  methods: {
    clear() {
      this.dependency.action = null;
      this.dependency.parameter = null;
      this.dependency.actionDependsOn = null;
      this.dependency.parameterDependsOn = null;

      this.$nextTick(() => {
        this.$refs.modalActionsDependenciesAdd.hide();
      });
    },
    add() {
      this.$store
        .dispatch("addSutActionDependency", {
          sut_id: this.sut.id,
          dependency: this.dependency
        })
        .then(() => {
          this.$root.$emit("bv::refresh::table", "sut-actions-dependencies");
          this.clear();
        });
    },
    updateParameters() {
      this.$store.dispatch("findSelectionParameters", {
        sut_id: this.sut.id,
        action_id: this.dependency.action
      });
    }
  },
  created: function() {
    this.$store.dispatch("findSelectionActions", { sut_id: this.sut.id });
  }
};
</script>