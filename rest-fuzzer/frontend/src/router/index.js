import Vue from "vue";
import VueRouter from "vue-router";

import Home from "../components/home/home";
import Suts from "../components/suts/suts";
import Projects from "../components/projects/projects"
import Dictionaries from "../components/dictionaries/dictionaries";
import Configurations from "../components/configurations/configurations";
import Reports from "../components/reports/reports";
import Tasks from "../components/tasks/tasks";
import About from "../components/about/about";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "home",
    component: Home
  },
  {
    path: "/suts",
    name: "suts",
    component: Suts
  },
  {
    path: "/suts/:id",
    name: "sut",
    component: Suts
  },
  {
    path: "/projects",
    name: "projects",
    component: Projects
  },
  {
    path: "/projects/:id",
    name: "project",
    component: Projects
  },
  {
    path: "/dictionaries",
    name: "dictionaries",
    component: Dictionaries
  },
  {
    path: "/dictionaries/:id",
    name: "dictionary",
    component: Dictionaries
  },
  {
    path: "/configurations",
    name: "configurations",
    component: Configurations
  },
  {
    path: "/configurations/:id",
    name: "configuration",
    component: Configurations
  },
  {
    path: "/reports",
    name: "reports",
    component: Reports
  },
  {
    path: "/reports/:id",
    name: "report",
    component: Reports
  },
  {
    path: "/tasks",
    name: "tasks",
    component: Tasks
  },
  {
    path: "/tasks/:id",
    name: "task",
    component: Tasks
  },
  {
    path: "/about",
    name: "about",
    component: About
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
