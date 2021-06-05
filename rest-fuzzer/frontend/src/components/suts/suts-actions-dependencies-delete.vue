<template>
  <b-modal
    id="suts-actions-dependencies-delete"
    ref="modal"
    centered
    v-if="this.dependency"
    size="xs"
  >
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete dependency
      </h6>
    </template>

    <template
      slot="default"
    >Are you sure you want to delete dependeny for '{{this.dependency.action.path}} - [{{this.dependency.action.httpMethod}}]'?</template>

    <template slot="modal-footer" slot-scope="{ cancel }">
      <div class="button-group-right">
        <b-button size="sm" variant="outline-danger" @click="confirm()">
          <b-icon icon="trash" font-scale="1"></b-icon>&nbsp; delete
        </b-button>
        <b-button size="sm" variant="outline-secondary" @click="cancel()">
          <b-icon icon="backspace" font-scale="1"></b-icon>&nbsp; cancel
        </b-button>
      </div>
    </template>
  </b-modal>
</template>

<script>
export default {
  props: ["sut", "dependency"],
  data() {
    return {};
  },
  methods: {
    confirm() {
      this.$store
        .dispatch("deleteSutActionDependency", {
          sut_id: this.sut.id,
          dependency: this.dependency
        })
        .then(() => {
          this.$root.$emit("bv::refresh::table", "sut-actions-dependencies");
          this.$refs['modal'].hide();
        });
    }
  }
};
</script>
