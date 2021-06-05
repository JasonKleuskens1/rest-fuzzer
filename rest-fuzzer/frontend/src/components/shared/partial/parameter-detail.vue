<template>
  <div>
    <dl class="row overview">
      <dt class="col-sm-2">identifier</dt>
      <dd class="col-sm-10">
        <b>#{{parameter.id}}</b>&nbsp;
        <b-badge v-if="parameter.required" variant="primary">required</b-badge>
      </dd>
      <dt class="col-sm-2">position</dt>
      <dd class="col-sm-10">{{parameter.position}}</dd>
      <dt class="col-sm-2">name</dt>
      <dd class="col-sm-10">{{parameter.name}}</dd>
      <dt class="col-sm-2">context</dt>
      <dd class="col-sm-10">{{parameter.context}}</dd>
      <dt class="col-sm-2">type</dt>
      <dd class="col-sm-10">{{parameter.type}}</dd>
      <dt class="col-sm-2">description</dt>
      <dd class="col-sm-10">{{parameter.description}}</dd>
      <dt class="col-sm-2">metadata</dt>
      <dd class="col-sm-10">
        <div class="json" :inner-html.prop="parameter.metaDataTuplesJson | json"></div>
      </dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: ["parameter"]
};
</script>