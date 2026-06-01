import dotenv from "dotenv";

dotenv.config();

type Config = {
	serverPort: number;
	testMessage: string;
};

export const config: Config = {
	serverPort: Number(getRequiredEnv("SERVER_PORT", "3000")),
	testMessage: getRequiredEnv("TEST_MESSAGE", "Default message"),
};

function getRequiredEnv(name: string, defaultValue: string | undefined): string {
	const value = process.env[name];

	if (!value) {
		if (!defaultValue) {
			throw new Error(`Missing environment variable: ${name}`);
		} else {
			return defaultValue;
		}
	}

	return value;
}
