<template>
  <div>
    <b-row style="margin-bottom:5px;">
      <b-col lg="2">
        <b-button
          size="sm"
          type="submit"
          variant="primary"
          v-b-modal.suts-actions-dependencies-add
          title="add a dependency"
        >
          <b-icon icon="plus" font-scale="1"></b-icon>&nbsp;add
        </b-button>
      </b-col>
      <b-col lg="10">
        <b-card bg-variant="white" header-tag="header" class="float-right clearfix">
          <template v-slot:header>
            <h6 class="mb-0">
              Filter: displaying
              <b>{{ totalRows }}</b> results.
            </h6>
          </template>
          <b-card-text>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="HTTP method(s):" label-for="input-http-method">
                <b-form-checkbox
                  class="float-left"
                  style="margin-right:10px;"
                  size="sm"
                  v-model="filter.httpMethods"
                  v-for="method in httpMethods"
                  :key="method"
                  :value="method"
                >{{ method }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.httpMethods = constants.HTTP_METHODS">select all</b-link>&nbsp;/
              <b-link @click="filter.httpMethods = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group
                label-size="sm"
                label="Discovery modus:"
                label-for="input-discovery-modus"
              >
                <b-form-checkbox
                  size="sm"
                  class="float-left"
                  style="margin-right:10px;"
                  v-model="filter.discoveryModes"
                  v-for="mode in discoveryModes"
                  :key="mode"
                  :value="mode"
                >{{ mode }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.discoveryModes = constants.DISCOVERY_MODES">select all</b-link>&nbsp;/
              <b-link @click="filter.discoveryModes = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="Path:" label-for="input-path">
                <b-input-group size="sm" label-for="input-path">
                  <b-form-input
                    v-model="filter.path"
                    size="sm"
                    type="search"
                    id="input-path"
                    placeholder="type to filter path"
                  ></b-form-input>
                  <b-input-group-append>
                    <b-button :disabled="!filter.path" @click="filter.path = ''">clear</b-button>
                  </b-input-group-append>
                </b-input-group>
              </b-form-group>
            </div>
          </b-card-text>
        </b-card>
      </b-col>
    </b-row>

    <b-table
      id="sut-actions-dependencies"
      class="table-sm"
      show-empty
      :busy="isBusy"
      striped
      :items="restProvider"
      :fields="fields"
      :borderless="true"
      :filter="filterToJson"
      :current-page="currentPage"
      :per-page="perPage"
    >
      <div slot="table-busy" class="text-center text-primary my-2">
        <b-spinner type="border" class="align-middle" small></b-spinner>
        <span style="margin-left:10px;">Loading...</span>
      </div>

      <template
        v-for="formatter in formatters"
        v-slot[`cell(${formatter.field})`]="data"
      >
        <template>{{ data.value | dynamicFilter($options.filters[formatter.as]) }}</template>
      </template>

      <template v-slot:cell(details)="row">
        <b-button size="sm" variant="primary" @click="handleToggle(row)">
          <b-icon v-if="row.detailsShowing" icon="x" font-scale="1"></b-icon>
          <b-icon v-if="!row.detailsShowing" icon="plus" font-scale="1"></b-icon>
        </b-button>
      </template>

      <template v-slot:row-details="row">
        <b-card>
          <ActionDepedencyDetail :sut="sut" :dependency="row.item"></ActionDepedencyDetail>
        </b-card>
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

    <SutsActionsDependenciesAdd :sut="this.sut"></SutsActionsDependenciesAdd>
  </div>
</template>

<script>
import Constants from "../../shared/constants";

import ActionDepedencyDetail from "../shared/partial/action-dependency-detail";

import SutsActionsDependenciesAdd from "./suts-actions-dependencies-add";

export default {
  props: ["sut", "fields", "formatters"],
  components: {
    ActionDepedencyDetail,
    SutsActionsDependenciesAdd
  },
  data() {
    return {
      constants: Constants,
      selectedDependency: null,
      isBusy: false,
      perPage: Constants.PER_PAGE,
      currentPage: 1,
      httpMethods: Constants.HTTP_METHODS,
      discoveryModes: Constants.DISCOVERY_MODES,
      filter: {
        httpMethods: Constants.HTTP_METHODS,
        discoveryModes: Constants.DISCOVERY_MODES,
        path: ""
      },
      firstTime: true
    };
  },
  methods: {
    restProvider(context, callback) {
      if (this.firstTime) {
        this.currentPage = 1;
        context.currentPage = 1;
        this.firstTime = false;
      } else {
        this.currentPage = context.currentPage;
      }
      return this.$store
        .dispatch("findSutActionsDependencies", {
          sut_id: this.sut.id,
          context: context
        })
        .then(() => {
          return this.$store.getters.suts.current.actions_dependencies.items;
        })
        .catch(() => {
          return [];
        });
    },
    linkGen(pageNum) {
      return pageNum === 1 ? "?" : `?page=${pageNum}`;
    },
    handleToggle(row) {
      this.selectedDependency = row.item;
      row.toggleDetails();
    }
  },
  computed: {
    filterToJson() {
      return encodeURI(JSON.stringify(this.filter));
    },
    totalRows() {
      return this.$store.getters.suts.current.actions_dependencies.count;
    },
    displayPagination() {
      return this.totalRows > this.perPage;
    }
  },
  created: function() {}
};
</script>

<style scoped>
ul {
  margin: 0px;
  padding: 0px;
}
</style>