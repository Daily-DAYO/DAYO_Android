![프로젝트소개](https://user-images.githubusercontent.com/65344669/224528950-53b6cca1-5b50-4f43-a364-fd6607776898.png)
<p align="center">
  <a href="https://github.com/Daily-DAYO/DAYO_Android/releases"><img src='https://img.shields.io/github/v/release/Daily-DAYO/DAYO_Android'></a>
  <a href="https://github.com/Daily-DAYO/DAYO_Android/issues"><img src='https://img.shields.io/github/issues/Daily-DAYO/DAYO_Android'></a>
  <a href="https://github.com/Daily-DAYO/DAYO_Android/graphs/contributors"><img src='https://img.shields.io/github/contributors/Daily-DAYO/DAYO_Android'></a>
  <a href='https://github.com/Daily-DAYO/DAYO_Android/blob/main/LICENSE'><img src='https://img.shields.io/github/license/Daily-DAYO/DAYO_Android'></a>
</p>

# 함께 즐기는 다이어리, DAYO📚
> 내가 꾸민 다이어리를 공유하고 다른 사람들과 정보를 나누는 커뮤니티 서비스

![메인기능소개](https://user-images.githubusercontent.com/65344669/224529293-a2105332-1ffe-4a6c-8d35-028764c38bf9.png)

## Language and Architecture
- Kotlin
- Clean Architecture With MVVM Pattern

## Team Members for Android
- 주윤겸(<a href="https://github.com/yuni-ju">@yuni-ju</a>)
- 허동준(<a href="https://github.com/DongJun-H">@DongJun-H</a>)

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:Daily-DAYO/DAYO_Android.git
```

## Application Version
- minSdkVersion : 26<br>
- targetSdkVersion : 36

## Git Convention
- Create issue<br>
- Create branch for each issue (feature/issue-#) -> eg) feature/issue-1<br>
- After develop merged, Delete branch<br>

## Commit Convention
- [feat] : Add new feature<br>
- [layout] : Add and modify layout and xml<br>
- [bug] : Error fix<br>
- [doc] : Add and modify documents
- [refactor] : code Refactoring
- [style] : NO CODE CHANGE. code formatting, convert tabs to spaces, etc
- [chore] : NO CODE CHANGE. updating grunt tasks, etc
- [test]  : Add test code
- <b>commit message rule</b>
    - Don't use a period
    - Write a command without using the past tense.
      example) "Fixed" -> "Fix", "Added" -> "Add"
- commit message Example : #issue number {issue title} e.g. #1 [layout] MainActivity<br>

## Xml Naming Convention
- Basic Principle : (what) _ (where) _ (description) _ (size)
- layout : (what) _ (where).xml
  e.g. activity_main.xml
- drawables : (what) _ (description) _ (size)
  e.g. ic_notify_icon_24
- ids : (what)_ (where) _ (description)
  e.g. tv_main_nickname
- strings : (where) _ (description)
- color : (description) _ (colorCode)
  e.g. white_FFFFFF<br>

- textView → tv / EditText → et / RecyclerView → rv / ImageView → img / Button → btn<br>
- state list drawable → selector_(where) _ (what)
  e.g. selector_main_button
- shape drawable → border _(color) _ ([fill/line/fill_line]) _ (radius)
  e.g. border_white_fill_12

## Copyright
Copyrightⓒ 2021- DAYO, All rights reserved.
