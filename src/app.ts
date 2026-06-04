import express from "express";
import { helloRouter } from "./api/helloController.js";
import { config } from "./config/config.js";

const app = express();

app.use(helloRouter);

app.listen({
	port: config.serverPort,
});
