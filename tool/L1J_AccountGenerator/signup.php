<?php
require '/lib/password.php';   // password_hash()はphp 5.5.0以降の関数のため、バージョンが古くて使えない場合に使用
require 'config.php';
// セッション開始
session_start();

// エラーメッセージ、登録完了メッセージの初期化
$errorMessage = "";
$signUpMessage = "";

// ログインボタンが押された場合
if (isset($_POST["signUp"])) {
    // 1. ユーザIDの入力チェック
    $errcheck = 1;
    if (empty($_POST["accountid"])) {  // 値が空のとき
        $errorMessage = 'アカウントIDが未入力です。';
    } else if (empty($_POST["password"])) {
        $errorMessage = 'パスワードが未入力です。';
    } else if (empty($_POST["password2"])) {
        $errorMessage = 'パスワードが未入力です。';
    } else if(!ctype_alnum($_POST["accountid"])){ //英数字
    	$errorMessage = 'アカウントIDは英数字のみを入力してください。';
    } else if(!ctype_alnum($_POST["password"])){ //英数字
    	$errorMessage = 'パスワードは英数字のみを入力してください。';
    } else if(strlen($_POST["accountid"]) < 4){ //長さ
    	$errorMessage = 'アカウントIDは4文字以上を入力してください。';
    } else if(strlen($_POST["password"]) < 4) { //長さ
    	$errorMessage = 'パスワードは4文字以上を入力してください。';
    }else{
    	$errcheck = 0;
    }

    if ($errcheck==0 && !empty($_POST["accountid"]) && !empty($_POST["password"]) && !empty($_POST["password2"]) && $_POST["password"] === $_POST["password2"]) {
        // 入力したユーザIDとパスワードを格納
        $accountid = $_POST["accountid"];
        $password = $_POST["password"];
        $ip       = $_SERVER["REMOTE_ADDR"];
        $hostname     = gethostbyaddr($ip);
        // 2. ユーザIDとパスワードが入力されていたら認証する
        $dsn = sprintf('mysql: host=%s; dbname=%s; charset=utf8', $db['host'], $db['dbname']);

        // 3. エラー処理
        try {
            $pdo = new PDO($dsn, $db['user'], $db['pass'],  array(PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION));

            // アカウントIDの重複を判断
            $sql = "SELECT * FROM accounts WHERE login = '$accountid'";  //入力したIDからユーザー名を取得
            $stmt = $pdo->query($sql);
            if ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            	$errorMessage = 'このアカウントIDは既に使用されています。';
            }else{
	            $stmt = $pdo->prepare("INSERT INTO accounts(login, password, ip, host, charslot) VALUES (?, ?, ?, ?, ?)");
	            $stmt->execute(array($accountid, password_hash($password, PASSWORD_DEFAULT), $ip, $hostname, $charslot));  // パスワードのハッシュ化を行う（今回は文字列のみなのでbindValue(変数の内容が変わらない)を使用せず、直接excuteに渡しても問題ない）
	            $_SESSION["ACCOUNTID"] = $accountid;
	            $_SESSION["IP"] = $ip;
	            header("Location: main.php");  // メイン画面へ遷移
	            exit();  // 処理終了
            }
        } catch (PDOException $e) {
            $errorMessage = 'データベースエラー';
            $e->getMessage();
            echo $e->getMessage();
        }
    } else if($_POST["password"] != $_POST["password2"]) {
        $errorMessage = 'パスワードに誤りがあります。';
    }
}
?>

<!DOCTYPE html>
<html >
<head>
  <meta charset="UTF-8">
  <title>L1J-JP 新規登録</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
  <link rel="stylesheet" href="css/style.css">

</head>

<body>
  <form class="sign-up" method="POST">
    <h1 class="sign-up-title">L1J-JP 新規登録</h1>
    <input type="text" class="sign-up-input" name="accountid" placeholder="アカウントID" autofocus>
    <input type="password" class="sign-up-input" name="password" placeholder="パスワード">
    <input type="password" class="sign-up-input" name="password2" placeholder="パスワード(確認)">
    <input type="submit" value="登録" class="login-button" name="signUp">
    <?php echo htmlspecialchars($errorMessage, ENT_QUOTES); ?>
  </form>

</body>
</html>