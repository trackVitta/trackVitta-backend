<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Welcome to Our Application</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .welcome-container {
            text-align: center;
            background: white;
            padding: 40px 60px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
        }
        p {
            color: #666;
            margin-bottom: 30px;
        }
        .btn-group {
            display: flex;
            gap: 20px;
            justify-content: center;
        }
        .btn {
            display: inline-block;
            padding: 12px 30px;
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            border-radius: 5px;
            transition: all 0.3s ease;
        }
        .btn-login {
            background-color: #007bff;
            color: white;
            border: 2px solid #007bff;
        }
        .btn-login:hover {
            background-color: #0056b3;
            border-color: #0056b3;
        }
        .btn-register {
            background-color: transparent;
            color: #28a745;
            border: 2px solid #28a745;
        }
        .btn-register:hover {
            background-color: #28a745;
            color: white;
        }
    </style>
</head>
<body>

<div class="welcome-container">
    <h1>Welcome</h1>
    <p>Please log into your existing account or create a new one to continue.</p>

    <div class="btn-group">
        <!-- Links directly to your JSP page routes -->
        <a href="/loginUserPage" class="btn btn-login">Login</a>
        <a href="/registrationUser" class="btn btn-register">Register</a>
    </div>
</div>

</body>
</html>
