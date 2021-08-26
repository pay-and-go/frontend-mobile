package com.example.payandgo

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

private val GRAPHQL_ENDPOINT: String = "http://proxy.payandgo.44.197.253.66.nip.io/graphql"

var okHttpClient = OkHttpClient.Builder().build()
val apolloClient = ApolloClient.builder()
    .serverUrl(GRAPHQL_ENDPOINT)
    .okHttpClient(okHttpClient)
    .build()