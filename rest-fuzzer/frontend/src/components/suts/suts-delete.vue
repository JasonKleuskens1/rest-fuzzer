<template>
  <b-modal id="suts-delete" ref="modal" centered v-if="this.sut" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete system under test
      </h6>
    </template>

    <template
      slot="default"
    >Are you sure you want to delete system under test '{{this.sut.location}}'?</template>

    <template slot="modal-footer" slot-scope="{ cancel }">
      <div class="button-group-right">
        <b-button size="sm" variant="outline-danger" @click="deleteSut()">
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
    sut() {
      return this.$store.getters.suts.current.item;
    }
  },
  methods: {
    deleteSut() {
      this.$store.dispatch("deleteSut", this.sut).then(() => {
        this.$router.push({ name: "suts" });
        this.$store.commit("set_sut_display", { display: null });
        this.$store.dispatch("findAllSuts");
      });
    }
  }
};
</script>
