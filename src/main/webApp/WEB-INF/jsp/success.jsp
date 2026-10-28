<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Successful</title>
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
        .success-container {
            text-align: center;
            background: white;
            padding: 40px 50px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
            max-width: 400px;
        }
        .icon-circle {
            width: 70px;
            height: 70px;
            background-color: #28a745;
            color: white;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 35px;
            font-weight: bold;
            margin: 0 auto 20px auto;
        }
        h1 {
            color: #28a745;
            margin-bottom: 10px;
            font-size: 24px;
        }
        p {
            color: #666;
            margin-bottom: 25px;
            line-height: 1.5;
        }
        .btn-login {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.2s ease;
        }
        .btn-login:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="success-container">
    <div class="icon-circle">✓</div>
    <h1>Success!</h1>
    <p>Your account has been created successfully. You can now log in using your credentials.</p>

    <!-- Link navigating back to your login endpoint -->
    <a href="/loginUserPage" class="btn-login">Go to Login</a>
</div>

</body>
</html>
