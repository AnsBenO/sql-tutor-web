async function showEventDeleteModal(eventId) {
	const response = await fetch(`/events/${eventId}/delete`);
	console.log("delete modal");
	if (response.ok) {
		const modalDiv = document.querySelector(".modalDiv");
		const text = await response.text();
		modalDiv.innerHTML = text;
		const modal = document.getElementById("deleteModal");
		modal.classList.remove("hidden-message");
		modal.classList.add("fade-in");
		modal.classList.remove("fade-out");
	} else {
		console.error(response.statusText);
	}
}
function closeModal() {
	const modal = document.getElementById("deleteModal");
	if (modal) {
		modal.classList.remove("fade-in");
		modal.classList.add("fade-out");
		setTimeout(() => {
			modal.classList.add("hidden-message");
		}, 500);
	}
}
