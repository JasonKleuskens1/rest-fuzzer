<template>
  <b-modal id="projects-clear" ref="modal" centered v-if="this.project" size="xs">
    <template slot="modal-header">
      <h6>
        <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;Clear results for fuzzing project
      </h6>
    </template>

    <template
      slot="default"
    >Are you sure you want to delete requests and responses for fuzzing project '{{this.project.type}}' for system under test '{{this.project.sut.title}}'?</template>

    <template slot="modal-footer" slot-scope="{ cancel }">
      <div class="button-group-right">
        <b-button size="sm" variant="outline-danger" @click="clear()">
          <b-icon icon="trash" font-scale="1"></b-icon>&nbsp; clear results
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
    clear() {
      this.$store.dispatch("clearProject", this.project);
    }
  }
};
</script>