import express from "express";
import { config } from "../config/config.js";

export const helloRouter = express.Router();

helloRouter.get("/", (_req, res, _next) => {
	res.send(`<h1>${config.testMessage}</h1>`);
});
