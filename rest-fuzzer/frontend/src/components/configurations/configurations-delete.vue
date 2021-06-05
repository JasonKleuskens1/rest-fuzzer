<template>
  <b-modal id="configuration-delete" ref="modal" v-if="configuration" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete configuration
      </h6>
    </template>

    <template slot="default">Are you sure you want to delete configuration '{{configuration.name}}'?</template>

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
  data() {
    return {};
  },
  computed: {
    configuration() {
      return this.$store.getters.configurations.current.item;
    }
  },
  methods: {
    confirm() {
      this.$store
        .dispatch("deleteConfiguration", this.configuration)
        .then(() => {
          this.$router.push({ name: "configurations" });
          this.$store.commit("set_configuration", { item: null });
          this.$store.dispatch("findAllConfigurations");
        });
    }
  }
};
</script>