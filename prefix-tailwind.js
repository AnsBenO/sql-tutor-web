import { spawn } from "child_process";

const tailwind = spawn(
	"npx",
	[
		"tailwindcss",
		"-i",
		"./src/main/resources/static/css/input.css",
		"-o",
		"./src/main/resources/static/css/output.css",
		"--watch",
	],
	{ shell: true }
);

tailwind.stdout.on("data", (data) => {
	process.stdout.write(`[TAILWIND] ${data}`);
});

tailwind.stderr.on("data", (data) => {
	process.stderr.write(`[TAILWIND] ${data}`);
});
