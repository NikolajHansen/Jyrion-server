import { useEffect } from 'react'
import { useAppDispatch } from '../store/hooks'
import { apiSlice } from '../api/apiSlice'

/**
 * Connects to /api/events via EventSource and invalidates RTK Query tags
 * based on incoming event types.
 */
export function useServerEvents() {
  const dispatch = useAppDispatch()

  useEffect(() => {
    const es = new EventSource('/api/events')

    es.addEventListener('player.statusChanged', (e: MessageEvent) => {
      try {
        const data = JSON.parse(e.data)
        if (data.playerId) {
          dispatch(apiSlice.util.invalidateTags([{ type: 'Player', id: data.playerId }]))
        }
      } catch {
        // ignore parse errors
      }
    })

    es.addEventListener('player.queueChanged', (e: MessageEvent) => {
      try {
        const data = JSON.parse(e.data)
        if (data.playerId) {
          dispatch(apiSlice.util.invalidateTags([{ type: 'Queue', id: data.playerId }]))
        }
      } catch {
        // ignore parse errors
      }
    })

    es.addEventListener('players.changed', () => {
      dispatch(apiSlice.util.invalidateTags(['PlayerList']))
    })

    return () => {
      es.close()
    }
  }, [dispatch])
}
