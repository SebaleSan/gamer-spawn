const feedbackTabButtons =
    document.querySelectorAll('.event-feedback-tab');
const feedbackPanels =
    document.querySelectorAll('.event-feedback-panel');
const requestedFeedbackTab =
    new URLSearchParams(window.location.search).get('tab');

function openFeedbackTab(tabName) {
    feedbackTabButtons.forEach(button => {
        button.classList.toggle(
            'active',
            button.dataset.tab === tabName);
    });
    feedbackPanels.forEach(panel => {
        panel.classList.toggle(
            'active',
            panel.id === 'feedback-' + tabName);
    });
}

feedbackTabButtons.forEach(button => {
    button.addEventListener('click', () => {
        openFeedbackTab(button.dataset.tab);
    });
});

if (requestedFeedbackTab === 'rating'
        || requestedFeedbackTab === 'comments') {
    openFeedbackTab(requestedFeedbackTab);
}

document.querySelectorAll(
    '.comment-menu-options form[action*="/edit"]'
).forEach(form => {
    const textarea = form.querySelector('textarea');
    const originalContent = textarea ? textarea.value : '';
    form.dataset.originalContent = originalContent;

    form.addEventListener('submit', event => {
        if (!textarea || textarea.value.trim() === originalContent.trim()) {
            event.preventDefault();
            const menu = form.closest('details');
            if (menu) {
                menu.open = false;
            }
        }
    });
});

document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', event => {
        if (!window.confirm(form.dataset.confirm)) {
            event.preventDefault();
        }
    });
});

document.querySelectorAll('.comment-cancel-button')
    .forEach(button => {
        button.addEventListener('click', () => {
            const textarea = document.getElementById(
                button.dataset.commentMenuId);
            const form = button.closest('form');
            const menu = button.closest('details');

            if (textarea && form?.dataset.originalContent !== undefined) {
                textarea.value = form.dataset.originalContent;
            }
            if (menu) {
                menu.open = false;
            }
        });
    });
