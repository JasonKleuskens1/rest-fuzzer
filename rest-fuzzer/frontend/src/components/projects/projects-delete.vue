<template>
  <b-modal id="projects-delete" ref="modal" centered v-if="this.project" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Delete fuzzing project
      </h6>
    </template>

    <template
      slot="default"
    >Are you sure you want to delete fuzzing project '{{this.project.type}}' for system under test '{{this.project.sut.title}}'?</template>

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
    project() {
      return this.$store.getters.projects.current.item;
    }
  },
  methods: {
    deleteSut() {
      this.$store.dispatch("deleteProject", this.project).then(() => {
        this.$router.push({ name: "projects" });
        this.$store.commit("set_project_display", { display: null });
        this.$store.dispatch("findAllProjects");
      });
    }
  }
};
</script>