document.addEventListener('DOMContentLoaded', () => {
    const todoForm = document.getElementById('todo-form');
    const todoList = document.getElementById('todo-list');

    // Загрузка задач при старте
    loadTodos();

    // Обработка формы
    todoForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const title = document.getElementById('title').value;
        const description = document.getElementById('description').value;

        createTodo({ title, description });
        todoForm.reset();
    });

    // Загрузка всех задач
    function loadTodos() {
        fetch('/api/todos')
            .then(response => response.json())
            .then(todos => {
                todoList.innerHTML = '';
                todos.forEach(todo => renderTodo(todo));
            })
            .catch(error => console.error('Error loading todos:', error));
    }

    // Создание новой задачи
    function createTodo(todo) {
        fetch('/api/todos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(todo)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(newTodo => renderTodo(newTodo))
            .catch(error => alert('Error creating todo: ' + JSON.stringify(error)));
    }

    // Обновление статуса задачи
    function updateTodoStatus(id, completed) {
        fetch(`/api/todos/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ completed })
        })
            .then(response => response.json())
            .then(() => loadTodos())
            .catch(error => console.error('Error updating todo:', error));
    }

    // Удаление задачи
    function deleteTodo(id) {
        fetch(`/api/todos/${id}`, {
            method: 'DELETE'
        })
            .then(() => loadTodos())
            .catch(error => console.error('Error deleting todo:', error));
    }

    // Отрисовка задачи
    function renderTodo(todo) {
        const li = document.createElement('li');
        li.className = 'todo-item' + (todo.completed ? ' completed' : '');

        li.innerHTML = `
            <div>
                <input type="checkbox" ${todo.completed ? 'checked' : ''}>
                <span class="title">${todo.title}</span> - ${todo.description}
            </div>
            <button>Delete</button>
        `;

        const checkbox = li.querySelector('input[type="checkbox"]');
        checkbox.addEventListener('change', () => updateTodoStatus(todo.id, checkbox.checked));

        const deleteButton = li.querySelector('button');
        deleteButton.addEventListener('click', () => deleteTodo(todo.id));

        todoList.appendChild(li);
    }
});