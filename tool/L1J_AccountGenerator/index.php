<?php
require '/lib/password.php';   // password_verfy()はphp 5.5.0以降の関数のため、バージョンが古くて使えない場合に使用
require 'config.php';

// セッション開始
session_start();

// エラーメッセージの初期化
$errorMessage = "";
// ログインボタンが押された場合
if (isset($_POST["login"])) {
    // 1. ユーザIDの入力チェック
    if (empty($_POST["accountid"])) {  // emptyは値が空のとき
    	$errorMessage = 'アカウントIDが未入力です。';
    } else if (empty($_POST["password"])) {
        $errorMessage = 'パスワードが未入力です。';
    }

    if (!empty($_POST["accountid"]) && !empty($_POST["password"])) {
        // 入力したユーザIDを格納
        $accountid = $_POST["accountid"];

        // 2. ユーザIDとパスワードが入力されていたら認証する
        $dsn = sprintf('mysql: host=%s; dbname=%s; charset=utf8', $db['host'], $db['dbname']);

        // 3. エラー処理
        try {
            $pdo = new PDO($dsn, $db['user'], $db['pass'], array(PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION));

            $stmt = $pdo->prepare('SELECT * FROM accounts WHERE login = ?');
            $stmt->execute(array($accountid));

            $password = $_POST["password"];

            if ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                if (password_verify($password, $row['password'])) {
                    session_regenerate_id(true);

                    // 入力したIDのユーザー名を取得
                    $sql = "SELECT * FROM accounts WHERE login = '$accountid'";  //入力したIDからユーザー名を取得
                    $stmt = $pdo->query($sql);
                    $_SESSION["ACCOUNTID"] = $row['login'];
                    $_SESSION["IP"] = $row['ip'];
                    header("Location: main.php");  // メイン画面へ遷移
                    exit();  // 処理終了
                } else {
                    // 認証失敗
                    $errorMessage = 'ユーザーIDあるいはパスワードに誤りがあります。';
                }
            } else {
                // 4. 認証成功なら、セッションIDを新規に発行する
                // 該当データなし
                $errorMessage = 'ユーザーIDあるいはパスワードに誤りがあります!';
            }
        } catch (PDOException $e) {
            $errorMessage = 'データベースエラー';
            $errorMessage = $sql;
             $e->getMessage();
             echo $e->getMessage();
        }
    }
} else if (isset($_POST["signup"])) {
	//サインアップページへ
	header("location: signup.php");
}
?>

<!DOCTYPE html>
<html >
<head>
  <meta charset="UTF-8">
  <title>L1J-JP ログイン</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
  <link rel="stylesheet" href="css/style.css">

</head>

<body>
  <form class="sign-up" method="POST">
    <h1 class="sign-up-title">L1J-JP ログイン</h1>
    <input type="text" class="sign-up-input" name="accountid" placeholder="アカウントID" autofocus>
    <input type="password" class="sign-up-input" name="password" placeholder="パスワード">
    <input type="submit" value="ログイン" class="login-button" name="login">
    <input type="submit" value="新規登録" class="sign-up-button" name="signup">
    <?php echo htmlspecialchars($errorMessage, ENT_QUOTES); ?>
  </form>

</body>
</html>

