<template>
  <b-modal id="reports-delete" ref="modal" centered v-if="this.report" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete report
      </h6>
    </template>

    <template slot="default">Are you sure you want to delete report '{{this.report.description }}'?</template>

    <template slot="modal-footer" slot-scope="{ cancel }">
      <div class="button-group-right">
        <b-button size="sm" variant="outline-danger" @click="deleteReport()">
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
    report() {
      return this.$store.getters.reports.current.item;
    }
  },
  methods: {
    deleteReport() {
      this.$store.dispatch("deleteReport", this.report).then(() => {
        this.$router.push({ name: "reports" });
        this.$store.commit("set_report_display", { display: null });
        this.$store.dispatch("findAllReports");
      });
    }
  }
};
</script>