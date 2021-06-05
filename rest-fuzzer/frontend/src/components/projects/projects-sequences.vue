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
              <b-form-group label-size="sm" label="Status(es):" label-for="input-sequence-status">
                <b-form-checkbox
                  class="float-left"
                  style="margin-right:15px;"
                  size="sm"
                  v-model="filter.sequenceStatuses"
                  v-for="status in sequenceStatuses"
                  :key="status"
                  :value="status"
                >{{ status }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.sequenceStatuses = sequenceStatuses">select all</b-link>&nbsp;/
              <b-link @click="filter.sequenceStatuses = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group
                label-size="sm"
                label="Sequence length(s):"
                label-for="input-sequence-length"
              >
                <b-form-checkbox
                  class="float-left"
                  style="margin-right:15px;"
                  size="sm"
                  v-model="filter.sequenceLengths"
                  v-for="length in sequenceLengths"
                  :key="length"
                  :value="length"
                >{{ length }}</b-form-checkbox>
              </b-form-group>
              <b-link @click="filter.sequenceLengths = sequenceLengths">select all</b-link>&nbsp;/
              <b-link @click="filter.sequenceLengths = []">select none</b-link>
            </div>
            <div class="float-left" style="margin-right:25px;">
              <b-form-group label-size="sm" label="Id:" label-for="input-id">
                <b-input-group size="sm" label-for="input-id">
                  <b-form-input
                    id="input-id"
                    v-model="filter.id"
                    size="sm"
                    type="search"
                    placeholder="type to filter id"
                  ></b-form-input>
                  <b-input-group-append>
                    <b-button :disabled="!filter.id" @click="filter.id = ''">clear</b-button>
                  </b-input-group-append>
                </b-input-group>
              </b-form-group>
            </div>
          </b-card-text>
        </b-card>
      </b-col>
    </b-row>

    <b-table
      id="project-sequences"
      class="table-sm"
      show-empty
      striped
      :busy.sync="isBusy"
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
        v-slot:[`cell(${formatter.field})`]="data"
      >
        <template>{{ data.value | dynamicFilter($options.filters[formatter.as]) }}</template>
      </template>

      <template v-slot:cell(details)="row">
        <b-button size="sm" variant="primary" @click="row.toggleDetails">
          <b-icon v-if="row.detailsShowing" icon="x" font-scale="1"></b-icon>
          <b-icon v-if="!row.detailsShowing" icon="plus" font-scale="1"></b-icon>
        </b-button>
      </template>

      <template v-slot:row-details="row">
        <b-card no-body>
          <b-tabs card pills vertical>
            <b-tab
              v-for="(request, index) in row.item.requests"
              :key="request.id"
              :title="tabTitle(index, request)"
            >
              <b-card-text>
                <b-card no-body>
                  <b-tabs card pills>
                    <b-tab title="Response" active>
                      <ResponseDetail :response="request.response"></ResponseDetail>
                    </b-tab>
                    <b-tab title="Request">
                      <RequestDetail :request="request"></RequestDetail>
                    </b-tab>
                    <b-tab title="REST model description">
                      <ActionDetail :action="request.action"></ActionDetail>
                    </b-tab>
                  </b-tabs>
                </b-card>
              </b-card-text>
            </b-tab>
          </b-tabs>
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

import ActionDetail from "../shared/partial/action-detail";
import RequestDetail from "../shared/partial/request-detail";
import ResponseDetail from "../shared/partial/response-detail";

export default {
  components: { ActionDetail, RequestDetail, ResponseDetail },
  props: ["project", "fields", "formatters"],
  data() {
    return {
      constants: Constants,
      isBusy: false,
      perPage: Constants.PER_PAGE,
      currentPage: 1,
      sequenceStatuses: Constants.SEQUENCE_STATUSES,
      sequenceLengths: Constants.SEQUENCE_LENGTHS,
      filter: {
        sequenceStatuses: Constants.SEQUENCE_STATUSES,
        sequenceLengths: Constants.SEQUENCE_LENGTHS,
        id: ""
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
        .dispatch("findProjectSequences", {
          id: this.project.id,
          context: context
        })
        .then(() => {
          return this.$store.getters.projects.current.sequences.items;
        })
        .catch(() => {
          return [];
        });
    },
    tabTitle(index, request) {
      return `#${index + 1} ${request.path} [${request.httpMethod}]`;
    }
  },
  computed: {
    filterToJson() {
      return encodeURI(JSON.stringify(this.filter));
    },
    totalRows() {
      return this.$store.getters.projects.current.sequences.count;
    },
    displayPagination() {
      return this.totalRows > this.perPage;
    }
  }
};
</script>
