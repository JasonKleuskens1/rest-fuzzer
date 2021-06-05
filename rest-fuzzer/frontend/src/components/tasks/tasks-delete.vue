<template>
  <b-modal id="task-delete" ref="modal" v-if="this.task !== null" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete task
      </h6>
    </template>

    <template slot="default">Are you sure you want to delete task '{{this.task.id}}'?</template>

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
    task() {
      return this.$store.getters.tasks.current.item;
    }
  },
  methods: {
    confirm() {
      this.$store.dispatch("deleteTask", this.task).then(() => {
        this.$router.push({ name: "tasks" });
        this.$store.commit("set_task", { item: null });
        this.$root.$emit("bv::refresh::table", "archive-tasks");
      });
    }
  }
};
</script>