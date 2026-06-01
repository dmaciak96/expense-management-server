import express from "express";
import { config } from "./config/config.js";
import { helloRouter } from "./routers/helloRouter.js";

const app = express();

app.use(helloRouter);

app.listen({
	port: config.serverPort,
});
