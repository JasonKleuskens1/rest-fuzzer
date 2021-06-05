<template>
  <div>
    <dl class="row overview">
      <dt class="col-sm-2">identifier</dt>
      <dd class="col-sm-10">
        <b>#{{response.id}}</b>
      </dd>
      <dt class="col-sm-2">HTTP status code</dt>
      <dd class="col-sm-10">{{response.httpStatusCode}}</dd>
      <dt class="col-sm-2">description</dt>
      <dd class="col-sm-10">{{response.description}}</dd>
    </dl>
  </div>
</template>

<script>
export default {
  props: ["response"]
};
</script>