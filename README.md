# 暗星之球 · 安卓安装包（云端一键打包）

这个工程把网页版《暗星之球》封装成一个**原生安卓 App**：画面在 WebView 里运行，但
WebView 的所有缩放通道都在代码里被关掉了（`setSupportZoom(false)`、`setBuiltInZoomControls(false)`、
`setTextZoom(100)`、`setUseWideViewPort(false)`），并且是沉浸式全屏、强制横屏、没有地址栏。

**所以它不会出现网页版那种"还能捏合缩放"的问题 —— 不是靠拦截，而是根本没有缩放通道。**

页面（`app/src/main/assets/index.html`）打包在 APK 内部，**离线可玩，不联网也照常运行**；
进度用 localStorage 存在手机上。

---

## 一、怎么打出 APK（不需要本机装 Android SDK）

打包在 GitHub 的服务器上完成，你只需要点几下网页。

1. 打开 https://github.com ，登录（没有账号就注册一个，免费）。
2. 右上角 **+ → New repository**，名字随便填（例如 `umbra-orb`），选 **Private** 也可以，点 **Create repository**。
3. 在新建的仓库页面点 **uploading an existing file**（或 Add file → Upload files）。
4. 把本 zip **解压后里面的所有文件和文件夹**一起拖进去（注意：要包含隐藏的 `.github` 文件夹）。
   - Windows 资源管理器默认不显示以点开头的文件夹，如果你看不到 `.github`，请先在
     「查看 → 显示 → 隐藏的项目」里打开它，否则工作流不会生效。
5. 点 **Commit changes**。
6. 打开仓库顶部的 **Actions** 标签：
   - 第一次会提示启用工作流，点 **I understand my workflows, go ahead and enable them**；
   - 左侧选 **Build APK** → 右侧点 **Run workflow** → 再点绿色的 **Run workflow**。
7. 等 3～6 分钟，任务变成绿色对勾后，点进这次运行，页面底部 **Artifacts** 里下载
   `umbra-orb-apk.zip`，解压得到：
   - `app-debug.apk`（推荐用这个，自带调试签名，直接能装）
   - `app-release.apk`

> 每次你修改仓库里的文件，工作流也会自动重新打包，不用手动点。

## 二、怎么装到手机

1. 把 `app-debug.apk` 传到手机（数据线、网盘、QQ 文件传输都行；微信有时会拦截 APK，建议用网盘或数据线）。
2. 手机上点开这个文件，系统会提示"禁止安装未知来源应用"，
   按提示给浏览器/文件管理器打开"允许安装未知应用"的开关，再点安装。
3. 装好后桌面上会出现 **暗星之球** 图标（金色星环），点开即玩，横屏全屏。

## 三、工程结构

```
umbra-orb-apk/
├─ .github/workflows/build-apk.yml   # 云端打包流程
├─ settings.gradle / build.gradle     # Gradle 配置（AGP 8.5.2）
├─ gradle.properties
└─ app/
   ├─ build.gradle                    # 无任何第三方依赖
   └─ src/main/
      ├─ AndroidManifest.xml          # 强制横屏、全屏主题、启动图标
      ├─ java/com/umbra/orb/MainActivity.java   # WebView 外壳（关闭缩放的核心代码）
      ├─ assets/index.html            # 游戏本体（单文件，无外链）
      └─ res/drawable/ic_launcher.xml # 矢量启动图标
```

## 四、想改什么改哪里

| 想改 | 改哪里 |
| --- | --- |
| 游戏内容 / 数值 / 画面 | 替换 `app/src/main/assets/index.html` |
| 应用名 | `app/src/main/res/values/strings.xml` |
| 图标 | `app/src/main/res/drawable/ic_launcher.xml`（矢量图，直接改路径） |
| 包名 | `app/build.gradle` 的 `applicationId` 与 `namespace`，以及 Java 的 package |
| 允许竖屏 | `AndroidManifest.xml` 里删掉 `android:screenOrientation` |
| 允许缩放（不建议） | `MainActivity.java` 里把 `setSupportZoom(false)` 改成 `true` |

## 五、关于 `apk-builder-cli`

`npm i -g apk-builder-cli` 这条路确实存在（v1.4.0，作者 `sudo-self`，通过 GitHub Actions 打包），
但它需要你提供带 `repo` 和 `workflow` 权限的 **GitHub Token**，并且用的是作者仓库里的**通用模板**：
模板的 WebView 默认开启 `setSupportZoom`，打出来的包**仍然可以捏合缩放**，还得自己改模板。

所以这里改成"自己的工程 + 自己的工作流"：只多花一次上传的时间，
换来的是**缩放从系统层面被关掉**、无地址栏、无第三方代码跑在你的账号里。
