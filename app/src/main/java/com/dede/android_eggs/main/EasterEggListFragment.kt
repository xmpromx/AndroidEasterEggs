package com.dede.android_eggs.main

import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import coil.load
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentEasterEggListBinding
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionController.Companion.EXTRA_O_POINT
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_G
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_H
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_I
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_J
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_K
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_L
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_M
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_N
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_O
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_O_POINT
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_P
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_Q
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_R
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_S
import com.dede.android_eggs.main.EggActionController.Companion.KEY_EGG_T
import com.dede.android_eggs.main.EggActionController.Companion.applySupportAdaptiveIcon
import com.dede.android_eggs.ui.adapter.VAdapter
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.adapter.VType
import com.dede.basic.requireDrawable
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3R


class EasterEggListFragment : Fragment(R.layout.fragment_easter_egg_list) {

    companion object {
        private val eggList = listOf(
            Egg(
                R.drawable.ic_android_udc,
                R.string.title_android_u,
                R.string.title_android_u,
                R.string.version_comment_android_u,
                R.string.target_class_android_t,
                true,
                itemType = 1
            ),
            Egg(
                R.drawable.ic_android_tiramisu,
                R.string.title_android_t,
                com.android_t.egg.R.string.t_egg_name,
                R.string.version_comment_android_t,
                R.string.target_class_android_t,
                true,
                KEY_EGG_T
            ),
            Egg(
                R.drawable.ic_android_s,
                R.string.title_android_s,
                com.android_s.egg.R.string.s_egg_name,
                R.string.version_comment_android_s,
                R.string.target_class_android_s,
                true,
                KEY_EGG_S
            ),
            Egg(
                com.android_r.egg.R.drawable.r_icon,
                R.string.title_android_r,
                com.android_r.egg.R.string.r_egg_name,
                R.string.version_comment_android_r,
                R.string.target_class_android_r,
                true,
                KEY_EGG_R
            ),
            Egg(
                com.android_q.egg.R.drawable.q_icon,
                R.string.title_android_q,
                com.android_q.egg.R.string.q_egg_name,
                R.string.version_comment_android_q,
                R.string.target_class_android_q,
                true,
                KEY_EGG_Q
            ),
            Egg(
                com.android_p.egg.R.drawable.p_icon,
                R.string.title_android_p,
                com.android_p.egg.R.string.p_app_name,
                R.string.version_comment_android_p,
                R.string.target_class_android_p,
                true,
                KEY_EGG_P
            ),
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o_1,
                com.android_o.egg.R.string.o_app_name,
                R.string.version_comment_android_o,
                R.string.target_class_android_o,
                true,
                KEY_EGG_O_POINT,
                bundleOf(EXTRA_O_POINT to true)
            ),
            Egg(
                R.drawable.ic_android_oreo,
                R.string.title_android_o,
                com.android_o.egg.R.string.o_app_name,
                R.string.version_comment_android_o,
                R.string.target_class_android_o,
                true,
                KEY_EGG_O
            ),
            Egg(
                R.drawable.ic_android_nougat,
                R.string.title_android_n,
                com.android_n.egg.R.string.n_app_name,
                R.string.version_comment_android_n,
                R.string.target_class_android_n,
                true,
                KEY_EGG_N
            ),
            Egg(
                R.drawable.ic_android_marshmallow,
                R.string.title_android_m,
                com.android_m.egg.R.string.m_mland,
                R.string.version_comment_android_m,
                R.string.target_class_android_m,
                true,
                KEY_EGG_M
            ),
            Egg(
                R.drawable.ic_android_lollipop,
                R.string.title_android_l,
                com.android_l.egg.R.string.l_lland,
                R.string.version_comment_android_l,
                R.string.target_class_android_l,
                true,
                KEY_EGG_L
            ),
            Egg(
                R.drawable.ic_android_kitkat,
                R.string.title_android_k,
                com.android_k.egg.R.string.k_dessert_case,
                R.string.version_comment_android_k,
                R.string.target_class_android_k,
                false,
                KEY_EGG_K
            ),
            Egg(
                R.drawable.ic_android_jelly_bean,
                R.string.title_android_j,
                com.android_j.egg.R.string.j_egg_name,
                R.string.version_comment_android_j,
                R.string.target_class_android_j,
                false,
                KEY_EGG_J
            ),
            Egg(
                R.drawable.ic_android_ics,
                R.string.title_android_i,
                com.android_i.egg.R.string.i_egg_name,
                R.string.version_comment_android_i,
                R.string.target_class_android_i,
                false,
                KEY_EGG_I
            ),
            Egg(
                R.drawable.ic_android_honeycomb,
                R.string.title_android_h,
                com.android_h.egg.R.string.h_egg_name,
                R.string.version_comment_android_h,
                R.string.target_class_android_h,
                false,
                KEY_EGG_H
            ),
            Egg(
                R.drawable.ic_android_gingerbread,
                R.string.title_android_g,
                com.android_g.egg.R.string.g_egg_name,
                R.string.version_comment_android_g,
                R.string.target_class_android_g,
                false,
                KEY_EGG_G
            ),
            Wavy(R.drawable.ic_wavy_line),
            Egg(
                R.drawable.ic_android_froyo,
                R.string.title_android_froyo,
                R.string.summary_android_froyo,
                R.string.version_comment_android_froyo
            ),
            Egg(
                R.drawable.ic_android_eclair,
                R.string.title_android_eclair,
                R.string.summary_android_eclair,
                R.string.version_comment_android_eclair
            ),
            Egg(
                R.drawable.ic_android_donut,
                R.string.title_android_donut,
                R.string.summary_android_donut,
                R.string.version_comment_android_donut
            ),
            Egg(
                R.drawable.ic_android_cupcake,
                R.string.title_android_cupcake,
                R.string.summary_android_cupcake,
                R.string.version_comment_android_cupcake
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.title_android_petit_four,
                R.string.summary_android_petit_four,
                R.string.version_comment_android_petit_four
            ),
            Egg(
                R.drawable.ic_android_classic,
                R.string.title_android_base,
                R.string.title_android_base,
                R.string.version_comment_android_base
            ),
            Wavy(R.drawable.ic_wavy_line_1, true),
        )
    }

    private lateinit var binding: FragmentEasterEggListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEasterEggListBinding.bind(view)
        binding.recyclerView.adapter = VAdapter(eggList) {
            addViewType(R.layout.item_easter_egg_layout, 0, EggHolder::class)
            addViewType(R.layout.item_easter_egg_layout, 1, PreviewHolder::class)
            addViewType(R.layout.item_easter_egg_wavy, -1, WavyHolder::class)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.recyclerView,
            OnApplyWindowInsetsListener { v, insets ->
                val edge = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(bottom = edge.bottom)
                return@OnApplyWindowInsetsListener insets
            })
    }

    private class WavyHolder(view: View) : VHolder<Wavy>(view) {
        private val imageView = itemView.findViewById<ImageView>(R.id.iv_icon)
        override fun onBindViewHolder(t: Wavy) {
            if (!t.repeat) {
                imageView.setImageResource(t.wavyRes)
                return
            }
            imageView.setImageDrawable(null)
            val bitmap = imageView.context.requireDrawable(t.wavyRes).toBitmap()
            val drawable = BitmapDrawable(imageView.resources, bitmap).apply {
                setTileModeXY(Shader.TileMode.REPEAT, null)
                setTint(MaterialColors.getColor(imageView, M3R.attr.colorSecondary))
            }
            imageView.background = drawable
        }
    }

    private class PreviewHolder(view: View) : EggHolder(view) {
        override fun onBindViewHolder(egg: Egg) {
            super.onBindViewHolder(egg)
            val context = itemView.context
            binding.tvSummary.text = EggActionController.getTimelineMessage(context)
            itemView.setOnClickListener {
                EggActionController.showTimelineDialog(
                    context, R.drawable.ic_android_udc, R.string.title_android_u
                )
            }
        }
    }

    private open class EggHolder(view: View) : VHolder<Egg>(view) {
        val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)

        private val eggActionController = EggActionController(itemView.context)

        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun onBindViewHolder(egg: Egg) {
            binding.tvTitle.setText(egg.eggNameRes)
            binding.tvSummary.setText(egg.androidRes)
            binding.ivIcon.load(egg.iconRes) {
                applySupportAdaptiveIcon(itemView.context, egg.supportAdaptiveIcon)
            }
            itemView.setOnClickListener { eggActionController.onClick(egg) }
            itemView.setOnLongClickListener { eggActionController.onLongPress(egg) }
        }
    }

    private class Wavy(val wavyRes: Int, val repeat: Boolean = false) : VType {
        override val viewType: Int = -1
    }

    data class Egg(
        @DrawableRes val iconRes: Int,
        @StringRes val androidRes: Int,
        @StringRes val eggNameRes: Int,
        @StringRes val versionCommentRes: Int,
        @StringRes val targetClassRes: Int = -1,
        val supportAdaptiveIcon: Boolean = false,
        val shortcutKey: String? = null,
        val extras: Bundle? = null,
        private val itemType: Int = 0,
    ) : VType {
        override val viewType: Int = itemType
    }

}