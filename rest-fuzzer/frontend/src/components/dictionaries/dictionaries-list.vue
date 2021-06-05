<template>
  <div>
    <b-card header-tag="header">
      <span slot="header">
        <b-icon icon="book" font-scale="1"></b-icon>&nbsp;Dictionaries
      </span>
      <b-card-text>
        <div class="button-group-left">
          <b-button size="sm" type="submit" variant="primary" @click="add()">
            <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
          </b-button>
        </div>
        <list
          @click-item="select"
          :select="false"
          :fields="fields"
          :items="dictionaries"
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
        { key: "name", thStyle: "width: 250px;" },
        { key: "items.length", label: "# Items" },
        { key: "createdAt", label: "Created @", thStyle: "width: 110px;" }
      ]
    };
  },
  methods: {
    select(item) {
      this.$router.push({ name: "dictionary", params: { id: item.id } });
      this.$store.commit("set_dictionary_display", { display: null });      
      this.$store.commit("set_dictionary", { item: item });
    },
    add() {
      this.$store.commit("set_dictionary", { item: null });      
      this.$store.commit("set_dictionary_display", { display: "add" });
    }
  },
  computed: {
    dictionaries() {
      return this.$store.getters.dictionaries.all.items;
    }
  },
  created: function() {
    this.$store.dispatch("findAllDictionaries");
  }
};
</script>
