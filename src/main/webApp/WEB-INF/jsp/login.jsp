<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        .form-container { width: 300px; margin: 50px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input { width: 100%; padding: 8px; box-sizing: border-box; }
        .btn { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; cursor: pointer; }
        .error-msg { color: red; margin-top: 10px; display: none; }
    </style>
</head>
<body>

<div class="form-container">
    <h2>Login</h2>
    <form id="loginForm">
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" required>
        </div>
        <button type="submit" class="btn">Sign In</button>
    </form>
    <div id="errorMessage" class="error-msg"></div>
</div>

<script>
    document.getElementById('loginForm').addEventListener('submit', async function(e) {
        e.preventDefault(); // Stop page reload

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorDiv = document.getElementById('errorMessage');

        errorDiv.style.display = 'none';

        // 1. Package the form data into a JavaScript object
        const payload = {
            email: email,
            password: password
        };

        try {
            // 2. Send it as JSON to your Spring Controller
            const response = await fetch('/login', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload) // Convert to JSON string
            });

            const data = await response.json();

            if (response.ok) {
                alert(data.message);
                // Example: Store token and redirect
                if (data.token) {
                    localStorage.setItem('token', data.token);
                }
                window.location.href = "/";
            } else {
                // Show the exact error JSON message sent back by your controller
                errorDiv.innerText = data.message || "Login failed.";
                errorDiv.style.display = 'block';
            }
        } catch (error) {
            errorDiv.innerText = "Network error occurred.";
            errorDiv.style.display = 'block';
        }
    });
</script>

</body>
</html>
