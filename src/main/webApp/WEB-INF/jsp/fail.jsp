<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Action Failed</title>
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
        .fail-container {
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
            background-color: #dc3545;
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
            color: #dc3545;
            margin-bottom: 10px;
            font-size: 24px;
        }
        p {
            color: #666;
            margin-bottom: 25px;
            line-height: 1.5;
        }
        .btn-retry {
            display: inline-block;
            background-color: #6c757d;
            color: white;
            padding: 12px 30px;
            font-size: 16px;
            font-weight: bold;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.2s ease;
        }
        .btn-retry:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>

<div class="fail-container">
    <div class="icon-circle">✕</div>
    <h1>Operation Failed</h1>
    <p>Something went wrong while processing your request. This could be due to invalid input or an already registered email address.</p>

    <!-- Link navigating back to your registration page to try again -->
    <a href="/registrationUser" class="btn-retry">Try Again</a>
</div>

</body>
</html>
