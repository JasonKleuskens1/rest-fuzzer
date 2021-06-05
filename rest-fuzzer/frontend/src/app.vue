<template>
  <div>
    <Menu></Menu>
    <div id="container">
      <router-view />
    </div>
    <Message></Message>
  </div>
</template>

<script>
import Menu from "./components/menu/menu";
import Message from "./components/shared/message";

export default {
  components: {
    Menu,
    Message
  },
  data() {
    return { data: {} };
  },
  created: function() {}
};
</script>

<style scoped>
#container {
  margin: auto;
  width: 98%;
}
</style>

<style lang="scss">
.table-hover tr {
  cursor: pointer;
}

.button-group-left {
  float: left;
  margin-bottom: 15px;
}
.button-group-left button,
.button-group-left a {
  margin-right: 10px;
}

.button-group-right {
  float: right;
  margin-bottom: 15px;
}
.button-group-right button,
.button-group-right a {
  margin-left: 10px;
}

.card-with-top-margin {
  margin-top: 35px;
}

th {
  font-size: 0.875rem;
}

td {
  font-size: 0.875rem;
}

.code {
  font-family: Consolas, Monaco, Lucida Console, Liberation Mono,
    DejaVu Sans Mono, Bitstream Vera Sans Mono, Courier New;
  white-space: pre-wrap;
  font-size: 0.85rem;
}

.json, .data {
  border: 1px solid #dddddd;
  padding: 10px;
  white-space: pre-wrap;
  font-size: 0.85rem;
}

.fixed {
  max-height: 550px;
  overflow: scroll;
}

.json .key {
  font-weight: 525;
}

.json .string {
  color: #0056b3;
  font-style: italic;
}

.json .number {
  color: #0056b3;
}

.json .true {
  color: #165b33;
}

.json .false {
  color: #bb2528;
}

dl.overview {
  margin: 20px 0px 20px 0px !important;
  line-height: 1.1em !important;
}

dl.overview dt {
  font-weight: normal !important;
}

dl.overview dd {
  font-style: italic;
}

.dl-div {
  margin: 20px 0px 20px 15px;
  line-height: 1.1em;
}

html {
  background: url("assets/background.jpg") no-repeat center center fixed;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}

body {
  background-color: transparent !important;
}

@import "../node_modules/bootstrap/scss/bootstrap";
@import "../node_modules/bootstrap-vue/src/index.scss";
</style>