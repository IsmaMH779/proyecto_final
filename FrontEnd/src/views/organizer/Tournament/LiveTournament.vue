<template>
<p>{{ route.params.id }}</p>
</template>

<script>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style>

</style>