<template>
  <div>
    <b-row style="margin-bottom:5px;">
      <b-col lg="12">
        <b-card bg-variant="white" header-tag="header" class="float-right clearfix">
          <template v-slot:header>
            <h6 class="mb-0">
              Filter: displaying
              <b>{{ totalRows }}</b> results.
            </h6>
          </template>
          <b-card-text>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="Context(s):" label-for="input-context">
                <b-form-checkbox
                  class="float-left"
                  style="margin-right:15px;"
                  size="sm"
                  v-model="filter.parameterContexts"
                  v-for="context in parameterContexts"
                  :key="context"
                  :value="context"
                >{{ context }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.parameterContexts = constants.PARAMETER_CONTEXTS">select all</b-link>&nbsp;/
              <b-link @click="filter.parameterContexts = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="Type(s)" label-for="input-type">
                <b-form-checkbox
                  class="float-left"
                  style="margin-right:15px;"
                  size="sm"
                  v-model="filter.parameterTypes"
                  v-for="type in parameterTypes"
                  :key="type"
                  :value="type"
                >{{ type }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.parameterTypes = constants.PARAMETER_TYPES">select all</b-link>&nbsp;/
              <b-link @click="filter.parameterTypes = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="Name:" label-for="input-name">
                <b-input-group size="sm" label-for="input-name">
                  <b-form-input
                    v-model="filter.name"
                    size="sm"
                    type="search"
                    id="input-name"
                    placeholder="type to filter name"
                  ></b-form-input>
                  <b-input-group-append>
                    <b-button :disabled="!filter.name" @click="filter.name = ''">clear</b-button>
                  </b-input-group-append>
                </b-input-group>
              </b-form-group>
            </div>
          </b-card-text>
        </b-card>
      </b-col>
    </b-row>

    <b-table
      id="sut-parameters"
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

      <template v-for="formatter in formatters" v-slot:[`(${formatter.field})`]="data">
        <template>{{ data.value | dynamicFilter($options.filters[formatter.as]) }}</template>
      </template>

      <template v-slot:cell(details)="row">
        <b-button size="sm" variant="primary" @click="row.toggleDetails">
          <b-icon v-if="row.detailsShowing" icon="x" font-scale="1"></b-icon>
          <b-icon v-if="!row.detailsShowing" icon="plus" font-scale="1"></b-icon>
        </b-button>
      </template>

      <template v-slot:row-details="row">
        <b-card>
          <b-card-text>
            <h6>Parameter details</h6>
            <ParameterDetail :parameter="row.item"></ParameterDetail>
          </b-card-text>
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
  </div>
</template>

<script>
import Constants from "../../shared/constants";

import ParameterDetail from "../shared/partial/parameter-detail";

export default {
  props: ["sut", "fields", "formatters"],
  components: { ParameterDetail },
  data() {
    return {
      constants: Constants,
      isBusy: false,
      perPage: Constants.PER_PAGE,
      currentPage: 1,
      parameterContexts: Constants.PARAMETER_CONTEXTS,
      parameterTypes: Constants.PARAMETER_TYPES,
      filter: {
        parameterContexts: Constants.PARAMETER_CONTEXTS,
        parameterTypes: Constants.PARAMETER_TYPES,
        name: ""
      },
      filterCopy: null
    };
  },
  methods: {
    restProvider(context, callback) {
      if (this.filterCopy != this.filterToJson) {
        this.currentPage = 1;
        context.currentPage = 1;
        this.filterCopy = this.filterToJson;
      } else {
        this.currentPage = context.currentPage;
      }

      return this.$store
        .dispatch("findSutParameters", {
          sut_id: this.sut.id,
          context: context
        })
        .then(() => {
          return this.$store.getters.suts.current.parameters.items;
        })
        .catch(() => {
          return [];
        });
    },
    linkGen(pageNum) {
      return pageNum === 1 ? "?" : `?page=${pageNum}`;
    }
  },
  computed: {
    filterToJson() {
      return encodeURI(JSON.stringify(this.filter));
    },
    totalRows() {
      return this.$store.getters.suts.current.parameters.count;
    },
    displayPagination() {
      return this.totalRows > this.perPage;
    }
  }
};
</script>