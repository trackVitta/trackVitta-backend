<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <style>
        .form-container { width: 350px; margin: 30px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; font-family: Arial, sans-serif; }
        .form-group { margin-bottom: 12px; }
        .form-group label { display: block; margin-bottom: 4px; font-weight: bold; }
        .form-group input { width: 100%; padding: 8px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .btn { width: 100%; padding: 10px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .btn:hover { background-color: #218838; }
        .message-box { margin-top: 10px; padding: 8px; border-radius: 4px; display: none; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    </style>
</head>
<body>

<div class="form-container">
    <h2>Create Account</h2>
    <form id="registerForm">
        <div class="form-group">
            <label for="firstName">First Name</label>
            <input type="text" id="firstName" required>
        </div>
        <div class="form-group">
            <label for="lastName">Last Name</label>
            <input type="text" id="lastName" required>
        </div>
        <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" id="email" required>
        </div>
        <div class="form-group">
            <label for="phone">Phone Number</label>
            <input type="tel" id="phone" required>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password</label>
            <input type="password" id="confirmPassword" required>
        </div>
        <button type="submit" class="btn">Register</button>
    </form>
    <div id="messageBox" class="message-box"></div>
</div>

<script>
    document.getElementById('registerForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const msgBox = document.getElementById('messageBox');
        msgBox.style.display = 'none';
        msgBox.className = 'message-box';

        // 1. Gather input values
        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const email = document.getElementById('email').value;
        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // 2. Client-side password validation
        if (password !== confirmPassword) {
            msgBox.innerText = "Passwords do not match!";
            msgBox.classList.add('error');
            msgBox.style.display = 'block';
            return;
        }

        // 3. Build the payload matching your UserDto structure
        const payload = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            phone: phone,
            password: password,
            createdAt: new Date().toISOString(), // Automatically tracks timing
            updatedAt: new Date().toISOString(),
            emailVerified: false
        };

        try {
            // 4. Send request to the registration path
            const response = await fetch('/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            // Parse response (Assuming 1 for success, 0 for failure based on your service code)
            const result = await response.json();

            if (response.ok && (result === 1 || result.status === 1)) {
                msgBox.innerText = "Registration successful! Redirecting to login...";
                msgBox.classList.add('success');
                msgBox.style.display = 'block';

                setTimeout(() => {
                    window.location.href = "login.jsp";
                }, 2000);
            } else {
                msgBox.innerText = "Registration failed. Email might already be registered.";
                msgBox.classList.add('error');
                msgBox.style.display = 'block';
            }
        } catch (error) {
            msgBox.innerText = "A network error occurred. Please try again.";
            msgBox.classList.add('error');
            msgBox.style.display = 'block';
        }
    });
</script>

</body>
</html>
