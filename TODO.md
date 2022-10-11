Android 13 的通知有点糊

英文字符串

滚动侧边栏

删除歌单中的歌





**搜索！（艺术家）**

**歌词**

歌词获取



播放的歌词页面

处理一下不可用的歌曲	*不太行*

同时获得多个歌曲的url	不太需要	

音乐音质	可以搞



在播放页那里做几个FAB去获取歌词，seek到当前歌曲

插入歌曲以及获取歌词的逻辑

把currentMetadata更改为currentPlaySong

歌单以及播放页面的topbar那里加上菜单栏





musicConnection那里可以改一下Flow

Compose Glance			

为什么在首页换的那个图片会显示不了？？？？？？？？？？？？？？？？？



断网时候retrofit处理

Youtube API



[UI]: https://dribbble.com/shots/16033507-Streamy-Podcast-App-Concept



感觉funtionTab可以写在一个page里面



***解决了卡顿！！！！！！！！！！！！！！！***



Client ID `358b9fc9bc3b4bc992325fef89633663`

Client Secret `efa34c3bc2924bd2ac2ba22cecea8114`

Media Session	done

Add Song List		done

Coming Soon		done

User Page			done

BottomSheet done

Play Page	done

渐变色	done

切换深色模式bug		done  但是界面还有点问题	 fixed

折叠标题栏 		done

获取艺术家的图片以及描述 some bugs  fixed

bug: minifyReleaseWithR8  https://github.com/square/retrofit/issues/3005		以后再解决吧 fixed！！！！

自定义主题 done

第一次进入App fetch本地数据  done

Animation			Wip

UI Improvement   always wip

随机播放	wip	done

Artist	  	选取较多歌曲的显示在首页 	done

历史播放	done

banner中的网络歌曲播放	fixed

playbackService bugs  fixed

导入网易云歌单 			done

导入歌单的对话框			done

控件的更新	done

通知代码重构	done

导入歌单之后的艺术家歌单要更改一下逻辑	done

网易云音乐的登录 done  but some bugs

homescreen ui improment wip

搜索多重匹配

协程的一些错误处理

code 重构



![img](https://assets.uigarage.net/content/uploads/2020/06/search_ios_wetv_uigarage.jpg.jpg)

compose version development











![Detailed view of MusicService](https://github.com/android/uamp/raw/main/docs/images/4-MusicService.png)

![Diagram showing how MediaController and MediaSession communicate](https://github.com/android/uamp/raw/main/docs/images/5-MediaController.png)

![Class diagram showing UAMP's Model-View-ViewModel architecture](https://github.com/android/uamp/raw/main/docs/images/9-mvvm.png)

![Diagram showing important interactions between UI classes](https://github.com/android/uamp/raw/main/docs/images/12-ui-class-diagram.png)