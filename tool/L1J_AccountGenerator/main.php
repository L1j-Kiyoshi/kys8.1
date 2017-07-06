<?php
require 'config.php';

session_start();

// ログイン状態チェック
if (!isset($_SESSION["ACCOUNTID"])) {
	header("Location: index.php");
	exit;
}

if (isset($_POST["logout"])) {
	// セッションの変数のクリア
	$_SESSION = array();
	// セッションクリア
	@session_destroy();
	//ログイン	ページへ
	header("location: index.php");
	exit;
}

// エラーメッセージ、登録完了メッセージの初期化
$errorMessage = "";
$signUpMessage = "";

if($_SESSION["IP"] != $_SERVER["REMOTE_ADDR"]){
	// 入力したユーザIDとパスワードを格納
	$accountid = $_SESSION["ACCOUNTID"];
	$ip       = $_SERVER["REMOTE_ADDR"];
	$hostname     = gethostbyaddr($ip);
	// 2. ユーザIDとパスワードが入力されていたら認証する
	$dsn = sprintf('mysql: host=%s; dbname=%s; charset=utf8', $db['host'], $db['dbname']);

	// 3. エラー処理
	try {
		$pdo = new PDO($dsn, $db['user'], $db['pass'],  array(PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION));

		$stmt = $pdo->prepare("UPDATE accounts SET ip = ?, host = ? WHERE login = ?");
		$stmt->execute(array($ip, $hostname, $accountid));
	} catch (PDOException $e) {
		$errorMessage = 'データベースエラー';
		$e->getMessage();
		echo $e->getMessage();
	}
}
?>

<!DOCTYPE html>
<html >
<head>
  <meta charset="UTF-8">
  <title>L1J-JP</title>

  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
  <link rel="stylesheet" href="css/style_main.css">

</head>

<body>
  <form class="sign-up" method="POST">
    <h1 class="sign-up-title">L1J-JP ログイン完了</h1>
	<?php echo 'このアカウントに紐付けられているIPは【' . $_SESSION["IP"] . '】です。<br>';?>
	<?php echo 'このページにログインした今のIPは【' . $_SERVER["REMOTE_ADDR"] . '】です。<br>';?>
	<br>
 	<?php
 		if($_SESSION["IP"] != $_SERVER["REMOTE_ADDR"]){
 			echo '紐づけられているIPと異なるためアカウント認証IPが現在のIPで更新されました。<br>';
 		}
 		echo ('リネージュへログイン可能です。<br>');
 		echo ('ゲーム内でログイン時は　ID:l1jjp / PASS:l1jjp　と入力してログインしてください。');
 	?>
 	<input type="submit" value="ログアウト" class="logout-button" name="logout">
  </form>

</body>
</html>