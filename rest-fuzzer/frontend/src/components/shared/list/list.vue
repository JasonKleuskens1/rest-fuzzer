<template>
  <div>
    <b-row style="margin-bottom:5px;">
      <b-col lg="6" class="my-1">
        <div v-if="displayFilter">
          <b-input-group size="sm">
            <b-form-input
              v-model="filter"
              type="search"
              id="filterInput"
              placeholder="type to filter table"
            ></b-form-input>
            <b-input-group-append>
              <b-button :disabled="!filter" @click="filter = ''">clear</b-button>
            </b-input-group-append>
          </b-input-group>
        </div>
      </b-col>
      <b-col lg="6" class="my-1"></b-col>
    </b-row>

    <b-table
      id="list"
      class="table-sm"
      show-empty
      :busy="isBusy"
      :selectable="select"
      select-mode="single"
      selectedVariant="primary"
      @row-selected="selectRow"
      @row-clicked="rowClicked"
      striped
      hover
      :items="items"
      :fields="fields"
      :borderless="true"
      :filter="filter"
      @filtered="onFiltered"
      :current-page="currentPage"
      :per-page="perPage"
    >
      <div slot="table-busy" class="text-center text-primary my-2">
        <b-spinner type="border" class="align-middle" small></b-spinner>
        <span style="margin-left:10px;">Loading...</span>
      </div>

      <template
        v-for="formatter in formatters"
        v-slot:[`cell(${formatter.field})`]="data"
      >
        <template>{{ data.value | dynamicFilter($options.filters[formatter.as]) }}</template>
      </template>

      <template v-slot:cell(actions)="row">
        <b-button size="sm" @click="row.toggleDetails">Show/hide details</b-button>
      </template>

      <template v-slot:row-details="row">
        <b-card>
          <ul>
            <li v-for="(value, key) in row.item.parameters" :key="key">{{ value }}</li>
          </ul>
          <ul>
            <li v-for="(value, key) in row.item.responses" :key="key">{{ value }}</li>
          </ul>
        </b-card>
      </template>

      <template slot="empty">No data present.</template>
    </b-table>

    <b-pagination
      v-if="!isBusy && displayPagination"
      size="sm"
      style="float:right;"
      v-model="currentPage"
      :total-rows="rows"
      :per-page="perPage"
      aria-controls="list"
    ></b-pagination>
  </div>
</template>

<script>
export default {
  props: ["items", "fields", "formatters", "displayFilter", "select"],
  data() {
    return {
      filter: null,
      perPage: 15,
      currentPage: 1,
      totalRows: null
    };
  },
  methods: {
    selectRow(item) {
      if (item.length == 0) {
        return;
      }
      this.$emit("select-item", item[0]);
    },
    rowClicked(item) {
      this.$emit("click-item", item);
    },
    linkGen(pageNum) {
      return pageNum === 1 ? "?" : `?page=${pageNum}`;
    },
    onFiltered(filteredItems) {
      this.totalRows = filteredItems.length;
      this.currentPage = 1;
    }
  },
  computed: {
    rows() {
      return this.items === null
        ? 0
        : this.totalRows !== null
        ? this.totalRows
        : this.items.length;
    },
    isBusy() {
      return this.items === null;
    },
    displayPagination() {
      if (this.items === null) {
        return false;
      } else {
        return this.rows > this.perPage;
      }
    }
  },
  mounted() {
    this.totalRows = this.items === null ? 0 : this.items.length;
  },
  created: function() {}
};
</script>
