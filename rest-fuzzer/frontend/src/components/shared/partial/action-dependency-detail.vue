<template>
  <div>
    <dl class="dl-horizontal">
      <dt>
        <b-button
          v-if="dependency.discoveryModus === constants.MANUAL"
          size="sm"
          type="submit"
          v-b-modal.suts-actions-dependencies-delete
          variant="outline-danger"
          title="delete this dependency"
        >
          <b-icon icon="trash" font-scale="1"></b-icon>&nbsp;delete
        </b-button>
      </dt>
    </dl>

    <h6>Depends on action:</h6>
    <dl class="row overview">
      <dt class="col-sm-2">identifier</dt>
      <dd class="col-sm-10">
        <b>#{{dependency.actionDependsOn.id}}</b>
      </dd>
      <dt class="col-sm-2">path</dt>
      <dd class="col-sm-10">{{dependency.actionDependsOn.path}}</dd>
      <dt class="col-sm-2">HTTP method</dt>
      <dd class="col-sm-10">{{dependency.actionDependsOn.httpMethod}}</dd>
    </dl>

    <hr />

    <h6>Depends on parameter:</h6>
    <ParameterDetail :parameter="dependency.parameter"></ParameterDetail>

    <hr />

    <h6>Information from response:</h6>
    <dl class="row overview">
      <dt class="col-sm-2">parameter</dt>
      <dd class="col-sm-10">{{ dependency.parameterDependsOn }}</dd>
    </dl>

    <DependencyDelete :sut="sut" :dependency="dependency"></DependencyDelete>
  </div>
</template>

<script>
import Constants from "../../../shared/constants";

import ParameterDetail from "../../shared/partial/parameter-detail";

import DependencyDelete from "../../suts/suts-actions-dependencies-delete";

export default {
  props: ["sut", "dependency"],
  components: { ParameterDetail, DependencyDelete },
  data() {
    return {
      constants: Constants
    };
  }
};
</script>