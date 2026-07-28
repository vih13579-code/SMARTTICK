(function () {
    function enhanceFileInput(input) {
        if (!input || input.dataset.fileInputEnhanced === "true") {
            return;
        }

        input.dataset.fileInputEnhanced = "true";

        var wrapper = document.createElement("div");
        wrapper.className = "smart-file-upload";

        var button = document.createElement("button");
        button.type = "button";
        button.className = "smart-file-upload-button";
        button.textContent = input.dataset.fileButtonLabel || "Choose file";

        var fileName = document.createElement("span");
        fileName.className = "smart-file-upload-name";
        fileName.textContent = input.dataset.fileEmptyLabel || "No file chosen";

        input.classList.add("smart-file-upload-native");
        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);
        wrapper.appendChild(button);
        wrapper.appendChild(fileName);

        button.addEventListener("click", function () {
            input.click();
        });

        input.addEventListener("change", function () {
            var names = Array.prototype.map.call(input.files || [], function (file) {
                return file.name;
            });
            fileName.textContent = names.length ? names.join(", ") : (input.dataset.fileEmptyLabel || "No file chosen");
        });
    }

    window.enhanceSmartFileInputs = function (root) {
        var scope = root || document;
        var inputs = scope.querySelectorAll("input[type='file'].smart-file-input");
        Array.prototype.forEach.call(inputs, enhanceFileInput);
    };

    function init() {
        window.enhanceSmartFileInputs(document);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
})();
