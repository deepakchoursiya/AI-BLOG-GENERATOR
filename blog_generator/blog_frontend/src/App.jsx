import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { PenTool, Sparkles, Send, Copy, Check } from 'lucide-react'
import axios from 'axios'
import './App.css'

function App() {
  const [topic, setTopic] = useState('')
  const [tone, setTone] = useState('professional')
  const [wordCount, setWordCount] = useState(500)
  const [blog, setBlog] = useState('')
  const [loading, setLoading] = useState(false)
  const [copied, setCopied] = useState(false)

  const generateBlog = async () => {
    if (!topic.trim()) return
    
    setLoading(true)
    try {
      const response = await axios.post('http://localhost:8080/api/blog/generate', {
        topic: topic,
        tone: tone,
        wordCount: wordCount
      })
      setBlog(response.data.content)
    } catch (error) {
      console.error('Error generating blog:', error)
      setBlog('Error generating blog. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const copyToClipboard = () => {
    navigator.clipboard.writeText(blog)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="app">
      <motion.div 
        className="container"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <motion.header 
          className="header"
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.2, duration: 0.5 }}
        >
          <PenTool className="header-icon" />
          <h1>AI Blog Generator</h1>
          <Sparkles className="sparkle" />
        </motion.header>

        <motion.div 
          className="input-section"
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.4, duration: 0.5 }}
        >
          <input
            type="text"
            placeholder="Enter your blog topic..."
            value={topic}
            onChange={(e) => setTopic(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && generateBlog()}
            className="topic-input"
          />
          
          <div className="controls-row">
            <div className="control-group">
              <label>Tone</label>
              <select
                value={tone}
                onChange={(e) => setTone(e.target.value)}
                className="tone-select"
              >
                <option value="professional">Professional</option>
                <option value="casual">Casual</option>
                <option value="friendly">Friendly</option>
                <option value="formal">Formal</option>
                <option value="humorous">Humorous</option>
                <option value="informative">Informative</option>
              </select>
            </div>
            
            <div className="control-group">
              <label>Word Count</label>
              <input
                type="number"
                min="100"
                max="2000"
                step="50"
                value={wordCount}
                onChange={(e) => setWordCount(parseInt(e.target.value))}
                className="word-count-input"
              />
            </div>
          </div>
          
          <motion.button
            onClick={generateBlog}
            disabled={loading || !topic.trim()}
            className="generate-btn"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            {loading ? (
              <motion.div 
                className="loading-spinner"
                animate={{ rotate: 360 }}
                transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
              />
            ) : (
              <Send size={20} />
            )}
            {loading ? 'Generating...' : 'Generate Blog'}
          </motion.button>
        </motion.div>

        <AnimatePresence>
          {blog && (
            <motion.div 
              className="output-section"
              initial={{ opacity: 0, y: 20, height: 0 }}
              animate={{ opacity: 1, y: 0, height: 'auto' }}
              exit={{ opacity: 0, y: -20, height: 0 }}
              transition={{ duration: 0.5 }}
            >
              <div className="output-header">
                <h3>Generated Blog</h3>
                <motion.button
                  onClick={copyToClipboard}
                  className="copy-btn"
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                >
                  {copied ? <Check size={16} /> : <Copy size={16} />}
                  {copied ? 'Copied!' : 'Copy'}
                </motion.button>
              </div>
              <motion.div 
                className="blog-content"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2, duration: 0.5 }}
              >
                {blog}
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </motion.div>
    </div>
  )
}

export default App
