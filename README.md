# News

*In this project, I built an android app to discover popular and recent News Headlines. I built clean UI, sync to a server, and present detailed information of each News Headline, allowing users to 'favorite' News Article.*
## NOTE: 
To run this app, you must also have an [API Key](https://newsapi.org) defined as a variable named `API_KEY` in the file `ArticleMediator.kt` located at `com.mayank_amr.news.data.repository.paging` package.

### Specifications:
- List of Top Headlines
     Country: India
     Default Tag: Technology, where 'Tags' represent 'Category' in the API. All categories in API docs are present in horizontally scroll.
- Sorted news by latest on top.
- Clicking on different tags should result in headlines from appropriate categories.
- Clicking `Card` Opens `Detail Screen`.

### Used:
- Used Pagination - lazy load at the end of every 10 cards.
- Used Room DB for saving favourites => Favourites should also be categorized by tags.
- Used Room DB for caching logic - Once cached, should work offline without network.
- Cached workflow works as same as online workflow .i.e., tag categorization with the cached data.
- Used Retrofit and Kotlin Flow for networking [and] to observe changes in Room and update the UI.
<!-- Added option in the UI to discard cached data.-->

### Edge workflow: 
- Open app when `offline` => `load cached data` => `scroll to bottom` => `turn on network` => `auto paginates`.
- Maintained proper scroll position when Room data is updated with new data from API.
- Creative recycler view interactions / animations and pull-to-refresh.


### Screenshots

#### Headlines with `Technology` tag
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/technology.jpg" width="350" title="Headlines with `Technology` tag">
</p>

#### Headline Detail screen
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/Detail.jpg" width="350" title="Headline Detail screen">
</p>

#### Headlines Loading State
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/loading.jpg" width="350" title="Headlines loading">
</p>

#### Pager loading next page
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/Page%20Loading.jpg" width="350" title="Pager loading next page">
</p>

#### At offline and pager unable to load next page
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/Offline%20and%20Next%20Page%20Not%20Loaded.jpg" width="350" title="At offline and pager unable to load next page">
</p>

#### Headlines with `Health` tag
<p align="center">
  <img src="https://github.com/Mayank-AMR/News/blob/main/Screenshots/Health.jpg" width="350" title="Headlines with `Health` tag" alt="accessibility text">
</p>

