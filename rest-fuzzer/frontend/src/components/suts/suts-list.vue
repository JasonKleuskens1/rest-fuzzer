<template>
  <div>
    <b-card header-tag="header">
      <span slot="header">
        <b-icon icon="display" font-scale="1"></b-icon>&nbsp;Systems under test
      </span>
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
          :items="suts"
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
      formatters: [{ field: "createdAt", as: "dateShort" }],
      fields: [
        { key: "id", label: "#", thStyle: "width: 50px;" },
        { key: "title", thStyle: "width: 250px;" },
        { key: "location", label: "OAS location" },
        { key: "createdAt", label: "Created @", thStyle: "width: 110px;" }
      ]
    };
  },
  methods: {
    select(sut) {
      this.$router.push({ name: "sut", params: { id: sut.id } });
      this.$store.commit("set_sut_display", { display: null });
      this.$store.dispatch("findSut", sut.id);
    },
    add() {
      this.$store.commit("set_sut", { item: null });
      this.$store.commit("set_sut_display", { display: "add" });
    }
  },
  computed: {
    suts() {
      return this.$store.getters.suts.all.items;
    }
  },
  created: function() {
    this.$store.dispatch("findAllSuts");
  }
};
</script>
