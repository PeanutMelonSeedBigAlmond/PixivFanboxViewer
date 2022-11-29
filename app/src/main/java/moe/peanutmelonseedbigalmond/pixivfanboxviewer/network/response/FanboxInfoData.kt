package moe.peanutmelonseedbigalmond.pixivfanboxviewer.network.response

data class FanboxInfoData(
    val user: UserInfoData,
    val creatorId:String,
    val description:String,
    val hasAdultContent:Boolean,
    val coverImageUrl:String,
    val profileLinks:List<String>,
)