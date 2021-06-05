<template>
  <div>
    <b-card header-tag="header">
      <template v-slot:header>
        <b-icon icon="tools" font-scale="1"></b-icon>&nbsp;Projects
      </template>
      <b-card-text>
        <div class="button-group-left">
          <b-button size="sm" type="submit" variant="primary" @click="add">
            <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
          </b-button>
        </div>
        <list
          @click-item="select"
          :select="false"
          :fields="fields"
          :items="projects"
          :formatters="formatters"
        ></list>
      </b-card-text>
    </b-card>
  </div>
</template>

<script>
import List from "../shared/list/list";

export default {
  components: { List },
  data() {
    return {
      formatters: [
        { field: "type", as: "enumToHuman" },
        { field: "createdAt", as: "dateShort" }
      ],
      fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "type", thStyle: "width: 250px;" },
        { key: "sut.title", label: "System under test" },
        { key: "createdAt", label: "Created @", thStyle: "width: 110px;" }
      ]
    };
  },
  methods: {
    select(project) {
      this.$router.push({ name: "project", params: { id: project.id } });
      this.$store.commit("set_project_display", { display: null });
      this.$store.dispatch("findProject", project.id);
    },
    add() {
      this.$store.commit("set_project", { item: null });
      this.$store.commit("set_project_display", { display: "add" });
    }
  },
  computed: {
    projects() {
      return this.$store.getters.projects.all.items;
    }
  },
  created: function() {
    this.$store.dispatch("findAllProjects");
  }
};
</script>
