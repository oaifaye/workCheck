$(function() {
	$("#flog").val("0");

	// 点击次数
	var pointCount = 0;
	
	// 需要点击次数
	var needCount = 0;

	// 随机的内容
	var content = 0;

	// 设置div greybackground的高与body一样
	var winHeight = $(document).height();

	// 开始
	var totleTime = 0;
	var time = 0;
	$("#action").click(function() {
		if (typeof ($("input[name=level]:checked").val()) == "undefined") {
			alert("请选择游戏模式！！！");
			return false;
		}
		// 初始化点击次数
		pointCount=0;
		if ($("input[name=level]:checked").val() == "0") {
			// 简单的
			time = 300;
			totleTime = time;
			// 初始化需要点击次数
			needCount = RandomInt(5,8);
		} else {
			time = 100;
			totleTime = time;
			// 初始化需要点击次数
			needCount = RandomInt(35, 40);
		}

		// 清空所有td
		$(".table td").text("");
		// 致标值位
		$("#flog").val("1");
		// 随机td中的内容
		var index = RandomInt(0, 15);
		content = RandomInt(50, 150);
		$(".table td").eq(index).text(content);
		// 需要点击次数
		$("#needCount").text("你需要准确点击有数字的方框 " + needCount + " 次");
	});

	// 时间

	window.setInterval(function() {
		if ($("#flog").val() == "1") {
			if (time > 0) {
				time--;
				$("#time").text("剩余时间: " + time + "秒");
			} else {
				 fail();
			}
		}
	}, 1000);

	// 结束
	$("#over").click(function() {
		if (pointCount == needCount&&needCount!=0) {
			// 致标值位
			$("#flog").val("0");

			if ($("input[name=level]:checked").val() == "0") {
				// 练习模式成功
				$("#greybackground").css({
					"height" : winHeight
				});
				$("#success1").css("display", "block");
				window.setTimeout(function() {
					window.location.reload();
				}, 5000);

			} else {
				// 正式模式成功
				$("#greybackground").css({
					"height" : winHeight
				});
				$(".success").css("display", "block");
			}
		} else if(pointCount != needCount&&needCount!=0) {
			 fail();
		}
	});

	// 成功后提交
	$("#successButton").click(function() {
		if ($("#name").val().trim() != "" && $("#comment").val().trim() != "") {
			$("#score").val(totleTime - time);
			window.submit.method = "post";
			window.submit.action = "saveSuanqiu.action";
			window.submit.submit();
		}else{
			alert("锤子，把空都填满咯！！！");
			return false;
		}
	});

	// td点击事件
	var jisuan=0;
	$(".table td").click(function() {
		if ($(this).text() != "") {
			var random = RandomInt(1, 10);
			if (random > 0 && random < 4) {
				// 30%几率答题
				$("#greybackground").css({
					"height" : winHeight
				});
				$("#answering1").css("display", "block");
				$(".table td").text("");
				jisuan=RandomInt(20, 30);
				$("#jisuan").text(jisuan);
			} else if (random > 3 && random < 5) {
				// 20%几率暂停3秒
				$("#greybackground").css({
					"height" : winHeight
				});
				$("#answering2").css("display", "block");
				window.setTimeout(function() {
					$("#greybackground").css({
						"height" : 0
					});
					$("#answering2").css("display", "none");
					addPoint();
				}, 3000);

			} else {
				addPoint();
			}
		}
	});

	// 鼠标移到td变背景时间
	$(".table td").hover(function() {
		if ($(this).text() != "") {
			$(this).css("background-color", "#000000");
			$(this).css("color", "#FFFFFF");
			$(this).css("cursor", "pointer");
		}
	}, function() {
		$(this).css("background-color", "#ffffff");
		$(this).css("color", "#000000");
		$(this).css("cursor", "default");
	});

	// 答题1提交
	$("#answerButton1").click(function() {
		
		if (parseInt($("#answer1").val().replace(" ", "")) == content+jisuan) {
			$("#greybackground").css({
				"height" : 0
			});
			$("#answering1").css("display", "none");
			addPoint();
			$("#answer1").val("");
		} else {
			fail();
		}
	});

	// 随机方法（调用方法）
	function RandomInt(min, max) {
		return parseInt(Math.random() * (max - min + 1) + min);
	}

	// 随即生成数并放到td里，点击次数+1（调用方法）
	function addPoint() {
		pointCount++;
		$(".table td").text("");
		var index = RandomInt(0, 15);
		content = RandomInt(50, 150);
		$(".table td").eq(index).text(content);
	}

	// 失败(调用方法)
	function fail() {
		$("#greybackground").css({
			"height" : winHeight
		});
		$("#fail").css("display", "block");
		window.setTimeout(function() {
			$("#answer1").val("");
			window.location.reload();
		}, 5000);
	}
	
//	 window.setInterval(function() {
//			
//	 $("#test").text(pointCount);
//	 }, 10);
});