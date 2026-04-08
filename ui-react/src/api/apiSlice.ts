import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

// ── Types ────────────────────────────────────────────────────────────────────

export interface HealthResponse {
  status: string
  timestamp: string
}

export interface Player {
  id: string
  name: string
  model: string
}

export interface PlayerStatus {
  playerId: string
  power: boolean
  playing: boolean
  volume: number
  track: Track | null
}

export interface Track {
  id: string
  title: string
  artist: string
  album: string
  durationMs: number
}

export interface QueuePage {
  offset: number
  limit: number
  total: number
  items: Track[]
}

export type PlayerCommand =
  | { type: 'play' }
  | { type: 'pause' }
  | { type: 'togglePause' }
  | { type: 'stop' }
  | { type: 'seek'; positionMs: number }
  | { type: 'setVolume'; volume: number }
  | { type: 'mute'; muted: boolean }
  | { type: 'queueClear' }
  | { type: 'queueJump'; index: number }
  | { type: 'queueAdd'; uri: string }

// ── API slice ────────────────────────────────────────────────────────────────

export const apiSlice = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({ baseUrl: '/api' }),
  tagTypes: ['PlayerList', 'Player', 'Queue', 'Indexer'],

  endpoints: (builder) => ({
    // ── Health ──────────────────────────────────────────────────────────────
    getHealth: builder.query<HealthResponse, void>({
      query: () => '/health',
    }),

    // ── Players ─────────────────────────────────────────────────────────────
    getPlayers: builder.query<Player[], void>({
      query: () => '/players',
      providesTags: (result) =>
        result
          ? [
              'PlayerList',
              ...result.map((p) => ({ type: 'Player' as const, id: p.id })),
            ]
          : ['PlayerList'],
    }),

    getPlayerStatus: builder.query<PlayerStatus, string>({
      query: (playerId) => `/players/${playerId}/status`,
      providesTags: (_result, _err, playerId) => [{ type: 'Player', id: playerId }],
    }),

    getQueue: builder.query<QueuePage, { playerId: string; offset?: number; limit?: number }>({
      query: ({ playerId, offset = 0, limit = 100 }) =>
        `/players/${playerId}/queue?offset=${offset}&limit=${limit}`,
      providesTags: (_result, _err, { playerId }) => [{ type: 'Queue', id: playerId }],
    }),

    // ── Commands (single endpoint) ───────────────────────────────────────────
    sendCommand: builder.mutation<void, { playerId: string; command: PlayerCommand }>({
      query: ({ playerId, command }) => ({
        url: `/players/${playerId}/commands`,
        method: 'POST',
        body: command,
      }),
      invalidatesTags: (_result, _err, { playerId, command }) => {
        const tags: Array<{ type: 'Player' | 'Queue'; id: string } | 'PlayerList'> = [
          { type: 'Player', id: playerId },
        ]
        if (
          command.type === 'queueClear' ||
          command.type === 'queueJump' ||
          command.type === 'queueAdd'
        ) {
          tags.push({ type: 'Queue', id: playerId })
        }
        return tags
      },
    }),
  }),
})

export const {
  useGetHealthQuery,
  useGetPlayersQuery,
  useGetPlayerStatusQuery,
  useGetQueueQuery,
  useSendCommandMutation,
} = apiSlice
