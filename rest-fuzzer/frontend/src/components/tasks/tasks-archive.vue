<template>
  <b-card header-tag="header">
    <template v-slot:header>
      <b-icon icon="list-task" font-scale="1"></b-icon>&nbsp;Tasks archive
    </template>

    <b-card-text>
      <b-table
        id="archive-tasks"
        class="table-sm"
        hover
        striped
        show-empty
        @row-clicked="select"
        :busy="isBusy"
        :items="restProvider"
        :fields="fields"
        :borderless="true"
        :current-page="currentPage"
        :per-page="perPage"
      >
        <div slot="table-busy" class="text-center text-primary my-2">
          <b-spinner type="border" class="align-middle" small></b-spinner>
          <span style="margin-left:10px;">Loading...</span>
        </div>

        <template v-slot:cell(status)="data">
          <div style="text-align:center;">
            <template v-if="data.value === constants.TASK_STATUS_QUEUED">
              <b-icon icon="clock" variant="success" font-scale="1"></b-icon>
            </template>
            <template v-if="data.value === constants.TASK_STATUS_RUNNING">
              <b-spinner small variant="success" type="grow"></b-spinner>
            </template>
            <template v-if="data.value === constants.TASK_STATUS_CRASHED">
              <b-icon icon="exclamation-circle-fill" variant="danger" font-scale="1"></b-icon>
            </template>
            <template v-if="data.value === constants.TASK_STATUS_FINISHED">
              <b-icon icon="check-circle" variant="success" font-scale="1"></b-icon>
            </template>
          </div>
        </template>

        <template v-slot:cell(startedAt)="data">
          <template>{{ data.value | dateShort }}</template>
        </template>

        <template v-slot:cell(endedAt)="data">
          <template>{{ data.value | dateShort }}</template>
        </template>

        <template slot="empty">No data present.</template>
      </b-table>

      <b-pagination
        v-if="displayPagination"
        size="sm"
        style="float:right;"
        v-model="currentPage"
        :total-rows="totalRows"
        :per-page="perPage"
        aria-controls="list"
      ></b-pagination>
    </b-card-text>
  </b-card>
</template>

<script>
import Constants from "../../shared/constants";

export default {
  components: { Constants },
  data() {
    return {
      isBusy: false,
      perPage: Constants.PER_PAGE,
      currentPage: 1,
      fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "name" },
        { key: "status", thStyle: "text-align:center; width: 70px;" },
        { key: "startedAt", label: "Started @", thStyle: "width: 100px;" },
        { key: "endedAt", label: "Ended @", thStyle: "width: 100px;" }
      ],
      constants: Constants
    };
  },
  methods: {
    select(task) {
      this.$router.push({ name: "task", params: { id: task.id } });
      this.$store.commit("set_task", { item: task });
    },
    restProvider(context, callback) {
      return this.$store
        .dispatch("findTasksArchive", {
          context: context
        })
        .then(() => {
          return this.$store.getters.tasks.archive.list;
        })
        .catch(() => {
          return [];
        });
    }
  },
  computed: {
    totalRows() {
      return this.$store.getters.tasks.archive.count;
    },
    displayPagination() {
      return this.totalRows > this.perPage;
    }
  }
};
</script>
